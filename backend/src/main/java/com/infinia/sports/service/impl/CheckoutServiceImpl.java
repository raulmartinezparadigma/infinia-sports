package com.infinia.sports.service.impl;

import com.infinia.sports.exception.ResourceNotFoundException;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de checkout
 */
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final com.infinia.sports.mail.OrderMailService orderMailService;
    
    // Tasa de impuesto por defecto (21% IVA)
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.21");

    @Override
    public void clearCart(String sessionId, String userId) {
        // Elimina todos los carritos asociados al usuario o sesión
        logger.info("[clearCart] Solicitando vaciado de carrito. userId={}, sessionId={}", userId, sessionId);
        if (userId != null && !userId.isEmpty()) {
            cartRepository.deleteByUserId(userId);
            logger.info("[clearCart] Carritos eliminados por userId={}", userId);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            cartRepository.deleteBySessionId(sessionId);
            logger.info("[clearCart] Carritos eliminados por sessionId={}", sessionId);
        } else {
            logger.warn("[clearCart] No se proporcionó userId ni sessionId válido para vaciar el carrito");
        }
    }

    @Override
    public Cart updateCartItemQuantity(String sessionId, String userId, String itemId, Integer quantity) {
        Cart cart = getCart(sessionId, userId);
        Optional<Cart.CartItem> optItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (optItem.isEmpty()) {
            logger.warn("[updateCartItemQuantity] No se encontró el itemId={} en el carrito", itemId);
            throw new ResourceNotFoundException("Producto no encontrado en el carrito");
        }
        Cart.CartItem item = optItem.get();
        if (quantity == null || quantity < 1) {
            // Eliminar el item si la cantidad es menor a 1
            cart.getItems().remove(item);
            logger.info("[updateCartItemQuantity] Item eliminado (cantidad <= 0): id={}", itemId);
        } else {
            item.setQuantity(quantity);
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            logger.info("[updateCartItemQuantity] Cantidad actualizada: id={}, nueva cantidad={}", itemId, quantity);
        }
        updateCartTotals(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        logger.info("[updateCartItemQuantity] Carrito guardado tras actualización de cantidad. ID: {}", savedCart.getId());
        return savedCart;
    }
    
    @Override
    public Cart addItemToCart(String sessionId, String userId, CartItemDTO cartItemDTO) {
        // Buscar carrito existente o crear uno nuevo
        Cart cart = getOrCreateCart(sessionId, userId);
        
        // Comprobar si el producto ya está en el carrito
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItemDTO.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Actualizar cantidad si el producto ya existe
            Cart.CartItem item = existingItem.get();
            logger.info("Actualizando cantidad del producto existente en el carrito: {} (cantidad +{})", item.getProductId(), cartItemDTO.getQuantity());
            item.setQuantity(item.getQuantity() + cartItemDTO.getQuantity());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            // Añadir nuevo producto al carrito
            logger.info("Añadiendo nuevo producto al carrito: {} ({} unidades)", cartItemDTO.getProductId(), cartItemDTO.getQuantity());
            Cart.CartItem newItem = Cart.CartItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(cartItemDTO.getProductId())
                    .productName(cartItemDTO.getProductName())
                    .quantity(cartItemDTO.getQuantity())
                    .unitPrice(cartItemDTO.getUnitPrice())
                    .totalPrice(cartItemDTO.getUnitPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())))
                    .attributes(cartItemDTO.getAttributes())
                    .build();
            
            cart.getItems().add(newItem);
        }
        
        // Actualizar totales
        updateCartTotals(cart);

        // Guardar y devolver el carrito actualizado
        cart.setUpdatedAt(LocalDateTime.now());
        logger.info("Guardando carrito con {} productos. ID de carrito: {}", cart.getItems().size(), cart.getId());
        Cart savedCart = null;
        try {
            savedCart = cartRepository.save(cart);
            logger.info("Carrito guardado correctamente en la base de datos. ID: {}", savedCart.getId());
        } catch (Exception e) {
            logger.error("Error al guardar el carrito en MongoDB: {}", e.getMessage(), e);
            throw e;
        }
        return savedCart;
    }

    @Override
    public Cart removeItemFromCart(String sessionId, String userId, String itemId) {
        // Obtener el carrito
        Cart cart = getCart(sessionId, userId);
        logger.info("[removeItemFromCart] Carrito encontrado: id={}, sessionId={}, userId={}, items={}", cart.getId(), cart.getSessionId(), cart.getUserId(), cart.getItems());
        logger.info("[removeItemFromCart] Intentando eliminar itemId={}", itemId);

        // Eliminar el producto del carrito
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        logger.info("[removeItemFromCart] Resultado de removeIf: {}", removed);

        if (!removed) {
            logger.warn("[removeItemFromCart] No se encontró el itemId={} en el carrito", itemId);
            throw new ResourceNotFoundException("Producto no encontrado en el carrito");
        }

        // Actualizar totales
        updateCartTotals(cart);

        // Guardar y devolver el carrito actualizado
        cart.setUpdatedAt(LocalDateTime.now());
        logger.info("[removeItemFromCart] Carrito actualizado y guardado tras eliminación de item. id={}", cart.getId());
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCart(String sessionId, String userId) {
        Cart cart;
        
        if (userId != null && !userId.isEmpty()) {
            // Buscar por ID de usuario
            cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> cartRepository.findBySessionId(sessionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado")));
            
            // Si se encontró por sesión pero no por usuario, actualizar el userId
            if (cart.getUserId() == null || cart.getUserId().isEmpty()) {
                cart.setUserId(userId);
                cart = cartRepository.save(cart);
            }
        } else {
            // Buscar solo por ID de sesión
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        }
        
        return cart;
    }

    @Override
    public Cart saveAddresses(String cartId, AddressDTO shippingAddress, AddressDTO billingAddress, boolean sameAsBillingAddress) {
        // Obtener el carrito
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        // Si las direcciones son iguales, usamos la misma para ambos casos
        
        // Crear y guardar la entidad Order
        Order order = mapToOrder(cart, shippingAddress, billingAddress);
        orderRepository.save(order);
        logger.info("Orden creada y guardada con ID: {}", order.getOrderId());
        
        return cart;
    }
    
    /**
     * Mapea los datos del carrito y las direcciones a una entidad Order
     * @param cart Carrito de compras
     * @param shippingAddress Dirección de envío
     * @param billingAddress Dirección de facturación
     * @return Entidad Order mapeada
     */
    private Order mapToOrder(Cart cart, AddressDTO shippingAddress, AddressDTO billingAddress) {
        // Crear la entidad Order con los campos requeridos
        Order order = new Order();
        // Vincular el ID del pedido y el orderId al id del carrito para trazabilidad
        order.setId(cart.getId());
        order.setOrderId(cart.getId());
        order.setLanguage("ES");
        order.setSubmitDate(LocalDateTime.now());
        order.setStatus("pending");
        order.setEmail(shippingAddress.getEmail());
        
        // Crear ShippingGroup con ID que empieza en 1
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId("1"); // Empezamos en 1 para el primer grupo
        shippingGroup.setShippingMethod("Infinia Sports");
        shippingGroup.setShippingCost(cart.getSubtotal());
        
        // Crear la lista de LineItems a partir de los CartItems
        List<Order.LineItem> lineItems = cart.getItems().stream()
                .map(cartItem -> {
                    // Crear un LineItem a partir del CartItem
                    return Order.LineItem.builder()
                        .id(cartItem.getId())
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .totalPrice(cartItem.getTotalPrice())
                        .attributes(cartItem.getAttributes())
                        .build();
                })
                .collect(Collectors.toList());
        
        // Asignar los LineItems al ShippingGroup
        shippingGroup.setLineItems(lineItems);
        
        // Añadir el ShippingGroup a la lista
        order.setShippingGroups(List.of(shippingGroup));
        
        // Mapear direcciones
        order.setShippingAddress(mapAddressDtoToOrderAddress(shippingAddress));
        order.setBillingAddress(mapAddressDtoToOrderAddress(billingAddress));
        
        // Configurar PriceInfo con todos los valores del Cart
        Order.PriceInfo priceInfo = new Order.PriceInfo();
        priceInfo.setSubtotal(cart.getSubtotal());
        priceInfo.setTax(cart.getTax());
        // Calcular el total como la suma del total del carrito
        priceInfo.setTotal(cart.getTotal());
        // Establecer discount en cero por defecto
        priceInfo.setDiscount(BigDecimal.ZERO);
        order.setPriceInfo(priceInfo);
        
        return order;
    }

    @Override
    public Order confirmOrder(CheckoutDTO checkoutDTO) {
        // Idempotencia: si ya existe una orden para este cartId/orderId, devuélvela
        java.util.Optional<Order> existing = orderRepository.findByOrderId(checkoutDTO.getCartId());
        if (existing.isPresent()) {
            logger.info("[confirmOrder] Ya existe una orden para orderId={}, devolviendo la existente", checkoutDTO.getCartId());
            return existing.get();
        }
        // Obtener el carrito
        Cart cart = cartRepository.findById(checkoutDTO.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        // Crear la orden
        Order order = createOrderFromCart(cart, checkoutDTO);
        
        // Guardar la orden
        Order savedOrder = orderRepository.save(order);

        // Vaciar el carrito tras confirmar el pedido
        // Si el carrito tiene userId, eliminar todos los carritos asociados a ese usuario para máxima limpieza (multi-dispositivo)
        if (cart.getUserId() != null && !cart.getUserId().isEmpty()) {
            cartRepository.deleteByUserId(cart.getUserId());
        } else if (cart.getSessionId() != null && !cart.getSessionId().isEmpty()) {
            // Si no hay userId, eliminar por sessionId
            cartRepository.deleteBySessionId(cart.getSessionId());
        } else {
            // Eliminar solo el carrito actual como fallback
            cartRepository.delete(cart);
        }
        // Nota: el frontend debe recargar el carrito tras el pedido para máxima sincronización
        return savedOrder;
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }

    /**
     * Envía el correo de resumen de pedido tras pago exitoso (centralizado)
     * @param orderId ID del pedido
     */
    @Override
    public void sendOrderConfirmationEmail(String orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado para envío de email"));
            String html = com.infinia.sports.mail.OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            String subject = "Resumen de tu pedido Infinia Sports #" + order.getOrderId();
            String to = order.getEmail();
            orderMailService.sendOrderSummary(to, subject, html);
            logger.info("[sendOrderConfirmationEmail] Email de resumen de pedido enviado a {} para orderId={}", to, orderId);
        } catch (jakarta.mail.MessagingException e) {
            logger.error("[sendOrderConfirmationEmail] Error enviando email de resumen de pedido: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[sendOrderConfirmationEmail] Error inesperado al generar/enviar email de pedido: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene un carrito existente o crea uno nuevo
     */
    private Cart getOrCreateCart(String sessionId, String userId) {
        Cart cart;
        
        if (userId != null && !userId.isEmpty()) {
            // Intentar encontrar por userId
            Optional<Cart> userCart = cartRepository.findByUserId(userId);
            
            if (userCart.isPresent()) {
                cart = userCart.get();
                // Actualizar sessionId si ha cambiado
                if (!sessionId.equals(cart.getSessionId())) {
                    cart.setSessionId(sessionId);
                }
            } else {
                // Intentar encontrar por sessionId
                Optional<Cart> sessionCart = cartRepository.findBySessionId(sessionId);
                
                if (sessionCart.isPresent()) {
                    cart = sessionCart.get();
                    // Actualizar userId
                    cart.setUserId(userId);
                } else {
                    // Crear nuevo carrito
                    cart = createNewCart(sessionId, userId);
                }
            }
        } else {
            // Buscar solo por sessionId
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> createNewCart(sessionId, null));
        }
        
        return cart;
    }
    
    /**
     * Crea un nuevo carrito
     */
    private Cart createNewCart(String sessionId, String userId) {
        Cart cart = Cart.builder()
                .sessionId(sessionId)
                .userId(userId)
                .items(new ArrayList<>())
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return cartRepository.save(cart);
    }
    
    /**
     * Actualiza los totales del carrito
     */
    private void updateCartTotals(Cart cart) {
        // Calcular subtotal
        BigDecimal subtotal = cart.getItems().stream()
                .map(Cart.CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular impuestos
        BigDecimal tax = subtotal.multiply(DEFAULT_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        
        // Actualizar totales en el carrito
        cart.setSubtotal(subtotal);
        cart.setTax(tax);
        cart.setTotal(subtotal.add(tax));
    }

    /**
     * Crea una orden a partir del carrito y los datos de checkout
     */
    private Order createOrderFromCart(Cart cart, CheckoutDTO checkoutDTO) {
        // Convertir items del carrito a items de orden
        List<Order.LineItem> lineItems = cart.getItems().stream()
                .map(cartItem -> Order.LineItem.builder()
                        .id(cartItem.getId())
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .totalPrice(cartItem.getTotalPrice())
                        .attributes(cartItem.getAttributes())
                        .build())
                .collect(Collectors.toList());
        
        // Crear grupo de envío con ID que empieza en 1
        Order.ShippingGroup shippingGroup = Order.ShippingGroup.builder()
                .id("1") // Empezamos en 1 para el primer grupo
                .shippingMethod(checkoutDTO.getShippingMethod())
                .shippingCost(BigDecimal.ZERO) // En un caso real, se calcularía según el método de envío
                .lineItems(lineItems)
                .build();
        
        // Crear información de precios
        Order.PriceInfo priceInfo = Order.PriceInfo.builder()
                .subtotal(cart.getSubtotal())
                .tax(cart.getTax())
                .discount(BigDecimal.ZERO) // En un caso real, se aplicarían descuentos si los hay
                .total(cart.getTotal())
                .build();
        
        // Crear información fiscal
        Order.TaxInfo taxInfo = Order.TaxInfo.builder()
                .taxRate(DEFAULT_TAX_RATE)
                .taxRegion("ES") // En un caso real, se determinaría según la dirección
                .build();
        
        // Convertir direcciones
        Order.Address shippingAddress = mapAddressDtoToOrderAddress(checkoutDTO.getShippingAddress());
        
        Order.Address billingAddress;
        if (checkoutDTO.isSameAsBillingAddress()) {
            billingAddress = shippingAddress;
        } else {
            billingAddress = mapAddressDtoToOrderAddress(checkoutDTO.getBillingAddress());
        }
        
        // Crear la orden
        return Order.builder()
                .orderId(cart.getId())
                .language("es") 
                .submitDate(LocalDateTime.now())
                .status("PENDIENTE")
                .email(checkoutDTO.getEmail())
                .shippingGroups(List.of(shippingGroup))
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .priceInfo(priceInfo)
                .taxInfo(taxInfo)
                .build();
    }
    
    /**
     * Convierte un AddressDTO a Order.Address
     */
    private Order.Address mapAddressDtoToOrderAddress(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        
        return Order.Address.builder()
                .firstName(addressDTO.getFirstName())
                .lastName(addressDTO.getLastName())
                .addressLine1(addressDTO.getAddressLine1())
                .addressLine2(addressDTO.getAddressLine2())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .phoneNumber(addressDTO.getPhoneNumber())
                .build();
    }
}
