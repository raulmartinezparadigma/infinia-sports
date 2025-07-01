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

    private static List<ShippingGroupDTO> toShippingGroupDTOList(List<Order.ShippingGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(OrderMapper::toShippingGroupDTO).collect(Collectors.toList());
    }

    private static ShippingGroupDTO toShippingGroupDTO(Order.ShippingGroup group) {
        if (group == null) return null;
        return ShippingGroupDTO.builder()
                .id(group.getId())
                .shippingMethod(group.getShippingMethod())
                .shippingCost(group.getShippingCost())
                .lineItems(toLineItemDTOList(group.getLineItems()))
                .build();
    }

    private static List<OrderLineItemDTO> toLineItemDTOList(List<Order.LineItem> items) {
        if (items == null) return null;
        return items.stream().map(OrderMapper::toLineItemDTO).collect(Collectors.toList());
    }

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
        shippingGroup.setShippingCost(cart.getSubtotal());
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
        // Delegar la creación base de la orden a fromCart para centralizar la lógica
        Order order = fromCart(cart, checkoutDTO.getShippingAddress(), checkoutDTO.getBillingAddress());

        // Sobrescribir o añadir detalles específicos del CheckoutDTO
        if (checkoutDTO.getShippingMethod() != null && !order.getShippingGroups().isEmpty()) {
            order.getShippingGroups().get(0).setShippingMethod(checkoutDTO.getShippingMethod());
        }

        return order;
    }
}
