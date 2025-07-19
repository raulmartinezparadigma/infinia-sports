package com.infinia.sports.service;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorageService imageStorageService; // Se mantiene por si se usa internamente

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setType(ProductType.CLOTHING);
        product1.setImageUrl("image1.jpg");

        product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setType(ProductType.SNEAKERS);
        product2.setImageUrl("image2.jpg");
    }

    @Test
    void testFindAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product1.getDescription(), result.get(0).getDescription());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        ProductDTO resultDto = productService.saveProduct(product1);

        assertNotNull(resultDto);
        assertEquals(product1.getId(), resultDto.getId());
        assertEquals(product1.getDescription(), resultDto.getDescription());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testDeleteProductById() {
        UUID id = product1.getId();
        doNothing().when(productRepository).deleteById(id);

        productService.deleteProductById(id);

        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        ProductDTO resultDto = productService.getProductById(product1.getId());

        assertNotNull(resultDto);
        assertEquals(product1.getId(), resultDto.getId());
        assertEquals(product1.getDescription(), resultDto.getDescription());
        verify(productRepository, times(1)).findById(product1.getId());
    }

    @Test
    void testGetProductById_NotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(id));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateProduct_Success() {
        Product details = new Product();
        details.setDescription("Updated Description");
        details.setPrice(new BigDecimal("15.50"));
        details.setImageUrl("updated.jpg");

        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO resultDto = productService.updateProduct(product1.getId(), details);

        assertNotNull(resultDto);
        assertEquals(product1.getId(), resultDto.getId());
        assertEquals("Updated Description", resultDto.getDescription());
        assertEquals("updated.jpg", resultDto.getImageUrl());
        assertEquals(0, new BigDecimal("15.50").compareTo(resultDto.getPrice()));
        verify(productRepository, times(1)).findById(product1.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(id, new Product()));
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProductByType() {
        when(productRepository.findByType(ProductType.CLOTHING)).thenReturn(Collections.singletonList(product1));

        List<ProductDTO> result = productService.getProductsByType(ProductType.CLOTHING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product1.getId(), result.get(0).getId());
        verify(productRepository, times(1)).findByType(ProductType.CLOTHING);
    }

    @Test
    void testGetProductsByDescription() {
        String searchTerm = "Description";
        when(productRepository.findByDescriptionContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> result = productService.getProductsByDescription(searchTerm);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findByDescriptionContainingIgnoreCase(searchTerm);
    }
}