package com.infinia.sports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Entidad que representa un pedido en el sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    
    private String orderId;
    private String language;
    private LocalDateTime submitDate;
    private String status;
    private String email;
    
    private List<ShippingGroup> shippingGroups;
    private Address shippingAddress;
    private Address billingAddress;
    
    private PriceInfo priceInfo;
    private TaxInfo taxInfo;
    
    /**
     * Clase interna que representa un grupo de envío
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingGroup {
        private String id;
        private String shippingMethod;
        private BigDecimal shippingCost;
        private List<LineItem> lineItems;
    }
    
    /**
     * Clase interna que representa una dirección
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String firstName;
        private String lastName;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phoneNumber;
    }
    
    /**
     * Clase interna que representa un ítem de línea en el pedido
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        @Transient
        private Product product;

        private String id;
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Map<String, String> attributes;
        private String productImageUrl;

    }
    
    /**
     * Clase interna que representa la información de precios
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceInfo {
        private BigDecimal subtotal;
        private BigDecimal tax;
        private BigDecimal discount;
        private BigDecimal total;
    }
    
    /**
     * Clase interna que representa la información fiscal
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxInfo {
        private BigDecimal taxRate;
        private String taxRegion;
        private Map<String, BigDecimal> taxBreakdown;
    }
}
