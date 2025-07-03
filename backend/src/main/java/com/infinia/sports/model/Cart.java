package com.infinia.sports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entidad que representa un carrito de compras
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class Cart {

    @Id
    private String id;
    
    @Indexed
    private String userId;     // ID del usuario autenticado
    private String sessionId;  // ID de sesión para usuarios no autenticados
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;  // Email del usuario para facilitar la comunicación
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private BigDecimal shippingCost;
    
    /**
     * Clase interna que representa un ítem en el carrito
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
        private String id;
        private String productId;
        private String productName;
        private String description;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Map<String, String> attributes;
        private String productImageUrl;
    }
}
