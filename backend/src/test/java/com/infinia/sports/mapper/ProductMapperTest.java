package com.infinia.sports.mapper;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {
    @Test
    void toDTO_mapsFieldsCorrectly() {
        Product product = new Product(
            "SKU123",
            UUID.randomUUID(),
            ProductType.CLOTHING,
            "desc",
            new BigDecimal("19.99"),
            "M",
            "img.jpg"
        );
        ProductDTO dto = ProductMapper.toDTO(product);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getSkuId(), dto.getSkuId());
        assertEquals(product.getDescription(), dto.getDescription());
        assertEquals(product.getSize(), dto.getSize());
        assertEquals(product.getImageUrl(), dto.getImageUrl());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(product.getType().name(), dto.getType());
    }
    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(ProductMapper.toDTO(null));
    }
    @Test
    void toDTOList_mapsListCorrectly() {
        Product p1 = new Product("A", UUID.randomUUID(), ProductType.CLOTHING, "d1", new BigDecimal("10"), "S", "img1");
        Product p2 = new Product("B", UUID.randomUUID(), ProductType.SNEAKERS, "d2", new BigDecimal("20"), "L", "img2");
        List<ProductDTO> dtos = ProductMapper.toDTOList(Arrays.asList(p1, p2));
        assertEquals(2, dtos.size());
        assertEquals("A", dtos.get(0).getSkuId());
        assertEquals("B", dtos.get(1).getSkuId());
    }
}
