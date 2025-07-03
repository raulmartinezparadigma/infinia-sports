package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para exponer la información pública del carrito al frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private String id;
    private String userId;
    private String sessionId;
    private String userEmail;
    private List<CartItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal total;
}
