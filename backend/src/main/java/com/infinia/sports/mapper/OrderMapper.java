package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.model.dto.OrderLineItemDTO;
import com.infinia.sports.model.dto.ShippingGroupDTO;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.repository.jpa.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;
import java.math.RoundingMode;

public class OrderMapper {
    private static final Logger logger = LoggerFactory.getLogger(OrderMapper.class);
    private static ProductRepository productRepository;
    
    public static void setProductRepository(ProductRepository repository) {
        productRepository = repository;
    }
    
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .email(order.getEmail())
                .language(order.getLanguage())
                .submitDate(order.getSubmitDate())
                .shippingGroups(toShippingGroupDTOList(order.getShippingGroups()))
                .shippingAddress(AddressMapper.toDTO(order.getShippingAddress()))
                .billingAddress(AddressMapper.toDTO(order.getBillingAddress()))
                .priceInfo(PriceInfoMapper.toDTO(order.getPriceInfo()))
                .taxInfo(TaxInfoMapper.toDTO(order.getTaxInfo()))
                .build();
    }

    /**
     * Convierte una lista de entidades ShippingGroup a una lista de DTOs.
     * @param groups Lista de entidades ShippingGroup.
     * @return Lista de ShippingGroupDTOs.
     */
    private static List<ShippingGroupDTO> toShippingGroupDTOList(List<Order.ShippingGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(OrderMapper::toShippingGroupDTO).collect(Collectors.toList());
    }

    /**
     * Convierte una entidad ShippingGroup a su DTO correspondiente.
     * @param group Entidad ShippingGroup.
     * @return ShippingGroupDTO.
     */
    private static ShippingGroupDTO toShippingGroupDTO(Order.ShippingGroup group) {
        if (group == null) return null;
        return ShippingGroupDTO.builder()
                .id(group.getId())
                .shippingMethod(group.getShippingMethod())
                .shippingCost(group.getShippingCost())
                .lineItems(toLineItemDTOList(group.getLineItems()))
                .build();
    }

    /**
     * Convierte una lista de entidades LineItem a una lista de DTOs, enriqueciendo con la URL de la imagen del producto.
     * @param items Lista de entidades LineItem.
     * @return Lista de OrderLineItemDTOs.
     */
    private static List<OrderLineItemDTO> toLineItemDTOList(List<Order.LineItem> items) {
        if (items == null) return null;
        return items.stream().map(item -> {
            try {
                Product product = productRepository.findById(UUID.fromString(item.getProductId())).orElse(null);
                if (product != null) {
                    item.setProductImageUrl(product.getImageUrl());
                }
            } catch (IllegalArgumentException e) {
                logger.error("[toLineItemDTOList] ID de producto no válido: {}. Error: {}", item.getProductId(), e.getMessage());
            }
            return toLineItemDTO(item);
        }).collect(Collectors.toList());
    }

    /**
     * Convierte una entidad LineItem a su DTO correspondiente.
     * @param item Entidad LineItem.
     * @return OrderLineItemDTO.
     */
    private static OrderLineItemDTO toLineItemDTO(Order.LineItem item) {
        if (item == null) return null;
        return OrderLineItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .attributes(item.getAttributes())
                .productImageUrl(item.getProductImageUrl())
                .build();
    }

    /**
     * Convierte un Cart y direcciones a una entidad Order
     * @param cart Carrito a convertir
     * @param shippingAddress Dirección de envío
     * @param billingAddress Dirección de facturación
     * @return Entidad Order mapeada
     */
    public static Order fromCart(Cart cart, AddressDTO shippingAddress, AddressDTO billingAddress) {
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
        shippingGroup.setShippingCost(cart.getShippingCost());
        // Crear la lista de LineItems a partir de los CartItems
        List<Order.LineItem> lineItems = cart.getItems().stream()
            .map(cartItem -> {
                String imageUrl = cartItem.getProductImageUrl(); // Priorizar la URL del CartItem

                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    logger.warn("[OrderMapper.fromCart] productImageUrl no encontrada en CartItem para Product ID: {}. Intentando obtenerla del repositorio.", cartItem.getProductId());
                    if (productRepository != null) {
                        try {
                            Product product = productRepository.findById(UUID.fromString(cartItem.getProductId()))
                                    .orElse(null);
                            if (product != null) {
                                imageUrl = product.getImageUrl();
                                logger.info("[OrderMapper.fromCart] URL de imagen obtenida del repositorio para Product ID: {}: {}", cartItem.getProductId(), imageUrl);
                            } else {
                                logger.warn("[OrderMapper.fromCart] Producto no encontrado en el repositorio con ID: {} durante el intento de fallback.", cartItem.getProductId());
                            }
                        } catch (IllegalArgumentException iae) {
                            logger.error("[OrderMapper.fromCart] ProductId inválido durante el fallback: {} no es un UUID válido.", cartItem.getProductId(), iae);
                        } catch (Exception e) {
                            logger.error("[OrderMapper.fromCart] Error inesperado durante el fallback al obtener producto con ID: {}.", cartItem.getProductId(), e);
                        }
                    } else {
                        logger.warn("[OrderMapper.fromCart] ProductRepository no está disponible para obtener la imagen del producto");
                    }
                } else {
                    logger.info("[OrderMapper.fromCart] Usando productImageUrl preexistente del CartItem para Product ID: {}: {}", cartItem.getProductId(), imageUrl);
                }

                return Order.LineItem.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .attributes(cartItem.getAttributes())
                    .productImageUrl(imageUrl) // Use the determined imageUrl
                    .build();
            })
            .collect(Collectors.toList());
        
        // Asignar los LineItems al ShippingGroup
        shippingGroup.setLineItems(lineItems);
        
        // Añadir el ShippingGroup a la lista
        order.setShippingGroups(List.of(shippingGroup));
        
        // Mapear direcciones
        order.setShippingAddress(AddressMapper.fromDTO(shippingAddress));
        order.setBillingAddress(AddressMapper.fromDTO(billingAddress));
        
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
    
    /**
     * Crea una orden a partir del carrito y los datos de checkout
     */
    public static Order fromCartAndCheckout(Cart cart, CheckoutDTO checkoutDTO) {
        Order order = new Order();
        order.setId(cart.getId()); // Asignar el ID del carrito al ID de la orden para consistencia
        order.setOrderId(cart.getId()); // Usamos el ID del carrito como ID de la orden para trazabilidad
        order.setUserId(cart.getUserId());
        order.setEmail(checkoutDTO.getEmail());
        order.setSubmitDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT");

        // Mapear dirección de envío
        order.setShippingAddress(toAddressEntity(checkoutDTO.getShippingAddress()));

        // Mapear items del carrito a lineItems del pedido
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId(UUID.randomUUID().toString());
        shippingGroup.setShippingMethod(checkoutDTO.getShippingMethod());

        // Usar el coste de envío calculado en el carrito
        BigDecimal shippingCost = cart.getShippingCost() != null ? cart.getShippingCost() : BigDecimal.ZERO;
        shippingGroup.setShippingCost(shippingCost);

        List<Order.LineItem> lineItems = cart.getItems().stream()
            .map(OrderMapper::fromCartItem)
            .collect(Collectors.toList());
        shippingGroup.setLineItems(lineItems);
        order.setShippingGroups(Collections.singletonList(shippingGroup));

        // Calcular y asignar PriceInfo
        Order.PriceInfo priceInfo = new Order.PriceInfo();
        BigDecimal subtotal = cart.getSubtotal() != null ? cart.getSubtotal() : BigDecimal.ZERO;
        BigDecimal tax = cart.getTax() != null ? cart.getTax() : BigDecimal.ZERO;
        
        priceInfo.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        priceInfo.setTax(tax.setScale(2, RoundingMode.HALF_UP));
        priceInfo.setDiscount(BigDecimal.ZERO); // Lógica de descuento no implementada aún

        BigDecimal total = cart.getTotal() != null ? cart.getTotal() : subtotal.add(tax).add(shippingCost);
        priceInfo.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        order.setPriceInfo(priceInfo);

        return order;
    }

    /**
     * Convierte un objeto CartItem del carrito a un LineItem del pedido.
     * @param cartItem El item del carrito.
     * @return El LineItem correspondiente para el pedido.
     */
    private static Order.LineItem fromCartItem(Cart.CartItem cartItem) {
        Order.LineItem lineItem = new Order.LineItem();
        lineItem.setId(cartItem.getId());
        lineItem.setProductId(cartItem.getProductId());
        lineItem.setProductName(cartItem.getProductName());
        lineItem.setQuantity(cartItem.getQuantity());
        lineItem.setUnitPrice(cartItem.getUnitPrice());
        lineItem.setTotalPrice(cartItem.getTotalPrice());
        lineItem.setAttributes(cartItem.getAttributes());
        lineItem.setProductImageUrl(cartItem.getProductImageUrl());
        return lineItem;
    }

    /**
     * Convierte un AddressDTO a una entidad de dirección para el pedido.
     * @param addressDTO El DTO de la dirección.
     * @return La entidad Address correspondiente.
     */
    private static Order.Address toAddressEntity(AddressDTO addressDTO) {
        if (addressDTO == null) return null;
        Order.Address address = new Order.Address();
        address.setFirstName(addressDTO.getFirstName());
        address.setLastName(addressDTO.getLastName());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        return address;
    }
}
