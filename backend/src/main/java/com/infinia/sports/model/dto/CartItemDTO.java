package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO para la gestión de ítems en el carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    
    private String id;
    
    @NotBlank(message = "El ID del producto es obligatorio")
    private String productId;
    
    private String productName;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer quantity;
    
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Map<String, String> attributes;
}
