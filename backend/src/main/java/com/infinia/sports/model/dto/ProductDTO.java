package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para exponer datos de producto al frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID id;
    private String skuId;
    private String description;
    private String size;
    private String imageUrl;
    private BigDecimal price;
    private String type; // Usamos String para simplificar serialización
}
