package com.infinia.sports.service.impl;

import com.infinia.sports.exception.ResourceNotFoundException;
import com.infinia.sports.mapper.CartMapper;
import com.infinia.sports.mapper.OrderMapper;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.*;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.CheckoutService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    public CheckoutServiceImpl(CartRepository cartRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    @Value("${infinia.sports.shipping-cost}")
    private BigDecimal shippingCost;

    // Tasa de impuesto por defecto (21% IVA)
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.21");

    @Override
    public void clearCart(String sessionId, String userId) {
        logger.info("[clearCart] Solicitando vaciado de carrito. userId={}, sessionId={}", userId, sessionId);
        if ((userId != null && !userId.isEmpty()) || (sessionId != null && !sessionId.isEmpty())) {
            cartRepository.deleteByUserIdOrSessionId(userId, sessionId);
            logger.info("[clearCart] Carritos eliminados por userId o sessionId: userId={}, sessionId={}", userId, sessionId);
        } else {
            logger.warn("[clearCart] No se proporcionó userId ni sessionId válido para vaciar el carrito");
        }
    }

    // --- Métodos privados para manejo interno de entidad ---
    /**
     * Obtiene el carrito existente por sessionId/userId, o crea uno nuevo si no existe.
     * Siempre devuelve un carrito persistido con id.
     */
    private Cart getCartEntity(String sessionId, String userId) {
        Cart cart;
        if (userId != null && !userId.isEmpty()) {
            cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> cartRepository.findBySessionId(sessionId)
                            .orElseGet(() -> createAndSaveNewCart(sessionId, userId)));
            if (cart.getUserId() == null || cart.getUserId().isEmpty()) {
                cart.setUserId(userId);
                cart = cartRepository.save(cart);
            }
        } else {
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> createAndSaveNewCart(sessionId, null));
        }
        return enrichCartItemsWithImages(cart);
    }

    /**
     * Crea y guarda un nuevo carrito vacío para la sesión/usuario dados.
     * Devuelve el carrito persistido con id.
     */
    private Cart createAndSaveNewCart(String sessionId, String userId) {
        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        newCart.setUserId(userId);
        newCart.setItems(new ArrayList<>());
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());
        logger.info("[createAndSaveNewCart] Creando nuevo carrito para sessionId={}, userId={}", sessionId, userId);
        Cart saved = cartRepository.save(newCart);
        logger.info("[createAndSaveNewCart] Nuevo carrito guardado con id={}", saved.getId());
        return saved;
    }

    @Override
    public CartDTO addItemToCart(String sessionId, String userId, CartItemDTO cartItemDTO) {
        Cart cart = getCartEntity(sessionId, userId);
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItemDTO.getProductId()))
                .findFirst();
        if (existingItem.isPresent()) {
            Cart.CartItem item = existingItem.get();
            logger.info("Actualizando cantidad del producto existente en el carrito: {} (cantidad +{})", item.getProductId(), cartItemDTO.getQuantity());
            item.setQuantity(item.getQuantity() + cartItemDTO.getQuantity());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP));
            if (cartItemDTO.getDescription() != null && !cartItemDTO.getDescription().isEmpty() && 
                (item.getDescription() == null || item.getDescription().isEmpty())) {
                item.setDescription(cartItemDTO.getDescription());
                logger.info("Actualizando descripción del producto en el carrito: {}", item.getProductId());
            }
            if (cartItemDTO.getProductName() != null && !cartItemDTO.getProductName().isEmpty() && 
                (item.getProductName() == null || item.getProductName().isEmpty())) {
                item.setProductName(cartItemDTO.getProductName());
                logger.info("Actualizando nombre del producto en el carrito: {}", item.getProductId());
            }
        } else {
            logger.info("Añadiendo nuevo producto al carrito: {} ({} unidades)", cartItemDTO.getProductId(), cartItemDTO.getQuantity());
            Cart.CartItem newItem = Cart.CartItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(cartItemDTO.getProductId())
                    .productName(cartItemDTO.getProductName())
                    .description(cartItemDTO.getDescription())
                    .quantity(cartItemDTO.getQuantity())
                    .unitPrice(cartItemDTO.getUnitPrice())
                    .totalPrice(cartItemDTO.getUnitPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP))
                    .attributes(cartItemDTO.getAttributes())
                    .build();
            cart.getItems().add(newItem);
        }
        updateCartTotals(cart);
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
        return CartMapper.toDTO(enrichCartItemsWithImages(savedCart));
    }

    @Override
    public CartDTO updateCartItemQuantity(String sessionId, String userId, String itemId, Integer quantity) {
        Cart cart = getCartEntity(sessionId, userId);
        Optional<Cart.CartItem> optItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (optItem.isEmpty()) {
            logger.warn("[updateCartItemQuantity] No se encontró el itemId={} en el carrito", itemId);
            throw new ResourceNotFoundException("Producto no encontrado en el carrito");
        }
        Cart.CartItem item = optItem.get();
        if (quantity == null || quantity < 1) {
            cart.getItems().remove(item);
            logger.info("[updateCartItemQuantity] Item eliminado (cantidad <= 0): id={}", itemId);
        } else {
            item.setQuantity(quantity);
            if (item.getUnitPrice() == null) {
                logger.warn("[updateCartItemQuantity] UnitPrice es null para itemId={}, usando 0", itemId);
                item.setUnitPrice(BigDecimal.ZERO);
            }
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
            if (item.getDescription() == null) {
                logger.info("[updateCartItemQuantity] Descripción es null para itemId={}", itemId);
            }
            logger.info("[updateCartItemQuantity] Cantidad actualizada: id={}, nueva cantidad={}, precio unitario={}, precio total={}", 
                    itemId, quantity, item.getUnitPrice(), item.getTotalPrice());
        }
        updateCartTotals(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        logger.info("[updateCartItemQuantity] Carrito guardado tras actualización de cantidad. ID: {}, items: {}", 
                savedCart.getId(), savedCart.getItems().size());
        return CartMapper.toDTO(enrichCartItemsWithImages(savedCart));
    }

    @Override
    public CartDTO removeItemFromCart(String sessionId, String userId, String itemId) {
        Cart cart = getCartEntity(sessionId, userId);
        logger.info("[removeItemFromCart] Carrito encontrado: id={}, sessionId={}, userId={}, items={}", cart.getId(), cart.getSessionId(), cart.getUserId(), cart.getItems());
        logger.info("[removeItemFromCart] Intentando eliminar itemId={}", itemId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        logger.info("[removeItemFromCart] Resultado de removeIf: {}", removed);
        if (!removed) {
            logger.warn("[removeItemFromCart] No se encontró el itemId={} en el carrito", itemId);
            throw new ResourceNotFoundException("Producto no encontrado en el carrito");
        }
        updateCartTotals(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        logger.info("[removeItemFromCart] Carrito actualizado y guardado tras eliminación de item. id={}", cart.getId());
        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toDTO(enrichCartItemsWithImages(savedCart));
    }

    @Override
    public CartDTO getCart(String sessionId, String userId) {
        return CartMapper.toDTO(getCartEntity(sessionId, userId));
    }

    @Override
    public CartDTO saveAddresses(String cartId, AddressDTO shippingAddress, AddressDTO billingAddress, boolean sameAsBillingAddress) {
        // Obtener el carrito
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        // Si las direcciones son iguales, usamos la misma para ambos casos
        
        // Crear y guardar la entidad Order
        Order order = OrderMapper.fromCart(cart, shippingAddress, billingAddress);
        orderRepository.save(order);
        logger.info("Orden creada y guardada con ID: {}", order.getOrderId());
        
        return CartMapper.toDTO(cart);
    }
    
    @Override
    public OrderDTO confirmOrder(CheckoutDTO checkoutDTO) {
        // Idempotencia: si ya existe una orden para este cartId/orderId, devuélvela
        java.util.Optional<Order> existing = orderRepository.findByOrderId(checkoutDTO.getCartId());
        if (existing.isPresent()) {
            logger.info("[confirmOrder] Ya existe una orden para orderId={}, devolviendo la existente", checkoutDTO.getCartId());
            return OrderMapper.toDTO(existing.get());
        }
        // Obtener el carrito
        Cart cart = cartRepository.findById(checkoutDTO.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        // Crear la orden usando el mapper centralizado
        Order order = OrderMapper.fromCartAndCheckout(cart, checkoutDTO);
        
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
        return OrderMapper.toDTO(savedOrder);
    }

    /**
     * Actualiza los totales del carrito
     */
    private void updateCartTotals(Cart cart) {
        // Calcular subtotal
        BigDecimal subtotal = cart.getItems().stream()
                .map(Cart.CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // La base imponible es el subtotal de productos más los gastos de envío
        BigDecimal taxableAmount = subtotal.add(this.shippingCost);

        // Calcular impuestos sobre la base imponible
        BigDecimal tax = taxableAmount.multiply(DEFAULT_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        
        // Actualizar totales en el carrito
        cart.setSubtotal(subtotal);
        cart.setTax(tax);
        cart.setShippingCost(this.shippingCost);
        cart.setTotal(taxableAmount.add(tax));
    }

    /**
     * Enriquece los items de un carrito con la URL de la imagen del producto.
     */
    private Cart enrichCartItemsWithImages(Cart cart) {
        if (cart == null) {
            logger.warn("[enrichCartItemsWithImages] Cart is null, cannot enrich.");
            return null;
        }
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            logger.info("[enrichCartItemsWithImages] Cart has no items to enrich. Cart ID: {}", cart.getId());
            return cart;
        }
        logger.info("[enrichCartItemsWithImages] Enriching cart ID: {}. Number of items: {}", cart.getId(), cart.getItems().size());

        for (Cart.CartItem item : cart.getItems()) {
            logger.info("[enrichCartItemsWithImages] Processing item with Product ID: {}", item.getProductId());
            if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
                logger.warn("[enrichCartItemsWithImages] Item has null or empty Product ID. Skipping enrichment for this item.");
                continue;
            }
            // Evitar búsqueda si la URL ya está presente (optimización)
            if (item.getProductImageUrl() != null && !item.getProductImageUrl().isEmpty()) {
                logger.info("[enrichCartItemsWithImages] ProductImageUrl already present for Product ID: {}. Skipping.", item.getProductId());
                continue;
            }
            try {
                Product product = productRepository.findById(UUID.fromString(item.getProductId()))
                        .orElse(null);
                if (product != null) {
                    logger.info("[enrichCartItemsWithImages] Found product for ID: {}. ImageUrl: {}, Description: {}",
                            item.getProductId(), product.getImageUrl(), product.getDescription());

                    // Establecer la URL de la imagen
                    item.setProductImageUrl(product.getImageUrl());

                    // Establecer la descripción del producto si no existe
                    if (item.getDescription() == null || item.getDescription().isEmpty()) {
                        item.setDescription(product.getDescription());
                        logger.info("[enrichCartItemsWithImages] Added description for product ID: {}", item.getProductId());
                    }

                    // Si el nombre del producto está vacío, usar la descripción del producto como nombre
                    if (item.getProductName() == null || item.getProductName().isEmpty()) {
                        item.setProductName(product.getDescription());
                        logger.info("[enrichCartItemsWithImages] Added name (from description) for product ID: {}", item.getProductId());
                    }
                } else {
                    logger.warn("[enrichCartItemsWithImages] Product not found in repository for ID: {}", item.getProductId());
                }
            } catch (IllegalArgumentException iae) {
                logger.error("[enrichCartItemsWithImages] Invalid Product ID format for ID: {}. Error: {}", item.getProductId(), iae.getMessage());
            } catch (Exception e) {
                logger.error("[enrichCartItemsWithImages] Error during product lookup for Product ID: {}. Error: {}", item.getProductId(), e.getMessage(), e);
            }
        }
        logger.info("[enrichCartItemsWithImages] Finished enriching cart ID: {}", cart.getId());
        return cart;
    }

    @Override
    public CartDTO linkCartToUser(String cartId, String userId, String userEmail) {
        logger.info("[linkCartToUser] Vinculando carrito {} con usuario {}", cartId, userId);
        
        // Buscar el carrito por su ID
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + cartId));
        
        // Actualizar los datos del usuario en el carrito
        cart.setUserId(userId);
        cart.setUserEmail(userEmail);
        cart.setUpdatedAt(LocalDateTime.now());
        
        // Guardar y devolver el carrito actualizado
        Cart updatedCart = cartRepository.save(cart);
        logger.info("[linkCartToUser] Carrito vinculado correctamente con usuario {}", userId);
        
        return CartMapper.toDTO(updatedCart);
    }

    /**
     * Método para inicializar el repositorio de productos en el OrderMapper
     */
    @PostConstruct
    public void init() {
        OrderMapper.setProductRepository(productRepository);
    }
}
