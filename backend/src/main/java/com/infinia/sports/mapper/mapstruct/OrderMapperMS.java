package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.model.dto.OrderLineItemDTO;
import com.infinia.sports.model.dto.ShippingGroupDTO;
import com.infinia.sports.repository.jpa.ProductRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper de MapStruct para Order con lógica compleja
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AddressMapperMS.class, PriceInfoMapperMS.class, TaxInfoMapperMS.class}
)
public abstract class OrderMapperMS {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderMapperMS.class);
    
    @Autowired
    protected ProductRepository productRepository;
    
    @Autowired
    protected AddressMapperMS addressMapper;
    
    /**
     * Convierte una entidad Order a OrderDTO
     */
    @Mapping(target = "shippingGroups", expression = "java(toShippingGroupDTOList(order.getShippingGroups()))")
    public abstract OrderDTO toDTO(Order order);
    
    /**
     * Convierte una lista de ShippingGroup a lista de DTOs
     */
    protected List<ShippingGroupDTO> toShippingGroupDTOList(List<Order.ShippingGroup> groups) {
        if (groups == null) return null;
        return groups.stream()
            .map(this::toShippingGroupDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Convierte un ShippingGroup a DTO
     */
    @Mapping(target = "lineItems", expression = "java(toLineItemDTOList(group.getLineItems()))")
    protected abstract ShippingGroupDTO toShippingGroupDTO(Order.ShippingGroup group);
    
    /**
     * Convierte una lista de LineItem a DTOs, enriqueciendo con imágenes
     */
    protected List<OrderLineItemDTO> toLineItemDTOList(List<Order.LineItem> items) {
        if (items == null) return null;
        return items.stream()
            .map(item -> {
                enrichLineItemWithImage(item);
                return toLineItemDTO(item);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Convierte un LineItem a DTO
     */
    protected abstract OrderLineItemDTO toLineItemDTO(Order.LineItem item);
    
    /**
     * Enriquece un LineItem con la URL de imagen del producto si no existe
     */
    protected void enrichLineItemWithImage(Order.LineItem item) {
        if (item.getProductImageUrl() == null || item.getProductImageUrl().trim().isEmpty()) {
            try {
                Product product = productRepository
                    .findById(UUID.fromString(item.getProductId()))
                    .orElse(null);
                if (product != null) {
                    item.setProductImageUrl(product.getImageUrl());
                }
            } catch (IllegalArgumentException e) {
                logger.error("[enrichLineItemWithImage] ID de producto no válido: {}. Error: {}", 
                    item.getProductId(), e.getMessage());
            } catch (Exception e) {
                logger.error("[enrichLineItemWithImage] Error al obtener producto: {}", 
                    item.getProductId(), e);
            }
        }
    }
    
    /**
     * Convierte un Cart y direcciones a una entidad Order
     */
    public Order fromCart(Cart cart, AddressDTO shippingAddress, AddressDTO billingAddress) {
        Order order = new Order();
        order.setId(cart.getId());
        order.setOrderId(cart.getId());
        order.setLanguage("ES");
        order.setSubmitDate(LocalDateTime.now());
        order.setStatus("pending");
        order.setEmail(shippingAddress.getEmail());
        
        // Crear ShippingGroup
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId("1");
        shippingGroup.setShippingMethod("Infinia Sports");
        shippingGroup.setShippingCost(cart.getShippingCost());
        
        // Mapear LineItems con enriquecimiento de imágenes
        List<Order.LineItem> lineItems = cart.getItems().stream()
            .map(this::cartItemToLineItem)
            .peek(this::enrichLineItemWithImage)
            .collect(Collectors.toList());
        
        shippingGroup.setLineItems(lineItems);
        order.setShippingGroups(List.of(shippingGroup));
        
        // Mapear direcciones usando el mapper de MapStruct
        order.setShippingAddress(addressMapper.fromDTO(shippingAddress));
        order.setBillingAddress(addressMapper.fromDTO(billingAddress));
        
        // Configurar PriceInfo
        Order.PriceInfo priceInfo = new Order.PriceInfo();
        priceInfo.setSubtotal(cart.getSubtotal());
        priceInfo.setShippingCost(cart.getShippingCost());
        priceInfo.setTax(cart.getTax());
        priceInfo.setTotal(cart.getTotal());
        priceInfo.setDiscount(BigDecimal.ZERO);
        order.setPriceInfo(priceInfo);
        
        return order;
    }
    
    /**
     * Crea una orden a partir del carrito y los datos de checkout
     */
    public Order fromCartAndCheckout(Cart cart, CheckoutDTO checkoutDTO) {
        Order order = new Order();
        order.setId(cart.getId());
        order.setOrderId(cart.getId());
        order.setUserId(cart.getUserId());
        order.setEmail(checkoutDTO.getEmail());
        order.setSubmitDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT");
        
        // Mapear dirección de envío
        order.setShippingAddress(addressMapper.fromDTO(checkoutDTO.getShippingAddress()));
        
        // Crear ShippingGroup
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId(UUID.randomUUID().toString());
        shippingGroup.setShippingMethod(checkoutDTO.getShippingMethod());
        
        BigDecimal shippingCost = cart.getShippingCost() != null ? 
            cart.getShippingCost() : BigDecimal.ZERO;
        shippingGroup.setShippingCost(shippingCost);
        
        // Mapear items
        List<Order.LineItem> lineItems = cart.getItems().stream()
            .map(this::cartItemToLineItem)
            .collect(Collectors.toList());
        shippingGroup.setLineItems(lineItems);
        order.setShippingGroups(Collections.singletonList(shippingGroup));
        
        // Configurar PriceInfo
        Order.PriceInfo priceInfo = new Order.PriceInfo();
        BigDecimal subtotal = cart.getSubtotal() != null ? cart.getSubtotal() : BigDecimal.ZERO;
        BigDecimal tax = cart.getTax() != null ? cart.getTax() : BigDecimal.ZERO;
        
        priceInfo.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        priceInfo.setTax(tax.setScale(2, RoundingMode.HALF_UP));
        priceInfo.setDiscount(BigDecimal.ZERO);
        
        BigDecimal total = cart.getTotal() != null ? 
            cart.getTotal() : subtotal.add(tax).add(shippingCost);
        priceInfo.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        order.setPriceInfo(priceInfo);
        
        return order;
    }
    
    /**
     * Convierte un CartItem a LineItem
     */
    protected Order.LineItem cartItemToLineItem(Cart.CartItem cartItem) {
        String imageUrl = cartItem.getProductImageUrl();
        
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            logger.warn("[cartItemToLineItem] productImageUrl no encontrada en CartItem para Product ID: {}. Intentando obtenerla del repositorio.", 
                cartItem.getProductId());
            if (productRepository != null) {
                try {
                    Product product = productRepository
                        .findById(UUID.fromString(cartItem.getProductId()))
                        .orElse(null);
                    if (product != null) {
                        imageUrl = product.getImageUrl();
                        logger.info("[cartItemToLineItem] URL de imagen obtenida del repositorio para Product ID: {}: {}", 
                            cartItem.getProductId(), imageUrl);
                    } else {
                        logger.warn("[cartItemToLineItem] Producto no encontrado en el repositorio con ID: {}", 
                            cartItem.getProductId());
                    }
                } catch (IllegalArgumentException iae) {
                    logger.error("[cartItemToLineItem] ProductId inválido: {} no es un UUID válido.", 
                        cartItem.getProductId(), iae);
                } catch (Exception e) {
                    logger.error("[cartItemToLineItem] Error inesperado al obtener producto con ID: {}.", 
                        cartItem.getProductId(), e);
                }
            } else {
                logger.warn("[cartItemToLineItem] ProductRepository no está disponible");
            }
        } else {
            logger.info("[cartItemToLineItem] Usando productImageUrl preexistente del CartItem para Product ID: {}: {}", 
                cartItem.getProductId(), imageUrl);
        }
        
        return Order.LineItem.builder()
            .id(cartItem.getId())
            .productId(cartItem.getProductId())
            .productName(cartItem.getProductName())
            .quantity(cartItem.getQuantity())
            .unitPrice(cartItem.getUnitPrice())
            .totalPrice(cartItem.getTotalPrice())
            .attributes(cartItem.getAttributes())
            .productImageUrl(imageUrl)
            .build();
    }
}
