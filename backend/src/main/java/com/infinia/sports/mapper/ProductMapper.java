package com.infinia.sports.mapper;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.ProductDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilidad para mapear Product a ProductDTO
 */
public class ProductMapper {
    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO(
            product.getId(),
            product.getSkuId(),
            product.getDescription(),
            product.getSize(),
            product.getImageUrl(),
            product.getPrice(),
            product.getType() != null ? product.getType().name() : null
        );
    }

    public static List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream().map(ProductMapper::toDTO).collect(Collectors.toList());
    }
}
