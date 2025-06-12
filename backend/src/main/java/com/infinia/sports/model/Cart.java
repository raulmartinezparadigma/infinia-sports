package com.infinia.sports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
    
    private String userId;
    private String sessionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    
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
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Map<String, String> attributes;
        private String productImageUrl;
    }
    
    /**
     * Método para calcular los totales del carrito
     */
    public void calculateTotals() {
        BigDecimal newSubtotal = BigDecimal.ZERO;
        
        for (CartItem item : items) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setTotalPrice(itemTotal);
            newSubtotal = newSubtotal.add(itemTotal);
        }
        
        this.subtotal = newSubtotal;
        // El impuesto se calculará en el servicio según las reglas fiscales aplicables
        this.total = this.subtotal.add(this.tax != null ? this.tax : BigDecimal.ZERO);
    }
}
