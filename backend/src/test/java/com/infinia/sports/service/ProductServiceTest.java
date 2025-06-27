package com.infinia.sports.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.impl.ProductServiceImpl;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);
        assertEquals(2, productService.getAllProducts().size());
    }

    @Test
    void testGetProductById_Success() {
        Product product = new Product();
        UUID id = UUID.randomUUID();
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        ProductDTO dto = productService.getProductById(id);
        assertEquals(product.getId(), dto.getId());
    }

    @Test
    void testGetProductById_NotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.getProductById(id));
    }

    @Test
    void testSaveProduct() {
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);
        assertNotNull(productService.saveProduct(product));
    }

    @Test
    void testDeleteProductById() {
        UUID id = UUID.randomUUID();
        productService.deleteProductById(id);
        verify(productRepository).deleteById(id);
    }

    @Test
    void testGetProductsByDescription() {
        Product p1 = new Product();
        Product p2 = new Product();
        when(productRepository.findByDescriptionContainingIgnoreCase("camiseta")).thenReturn(Arrays.asList(p1, p2));
        List<ProductDTO> result = productService.getProductsByDescription("camiseta");
        assertEquals(2, result.size());
    }

    @Test
    void testGetProductsByDescription_Empty() {
        when(productRepository.findByDescriptionContainingIgnoreCase("noexiste")).thenReturn(Arrays.asList());
        List<ProductDTO> result = productService.getProductsByDescription("noexiste");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetProductsBySize() {
        Product p1 = new Product();
        when(productRepository.findBySize("M")).thenReturn(Arrays.asList(p1));
        List<ProductDTO> result = productService.getProductsBySize("M");
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsBySize_Empty() {
        when(productRepository.findBySize("XXXL")).thenReturn(Arrays.asList());
        List<ProductDTO> result = productService.getProductsBySize("XXXL");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testImportProducts() {
        Product p1 = new Product();
        Product p2 = new Product();
        List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.saveAll(products)).thenReturn(products);
        List<ProductDTO> result = productService.importProducts(products);
        assertEquals(2, result.size());
    }

    @Test
    void testImportProducts_EmptyList() {
        List<Product> products = Arrays.asList();
        when(productRepository.saveAll(products)).thenReturn(products);
        List<ProductDTO> result = productService.importProducts(products);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testUpdateProduct_Success() {
        UUID id = UUID.randomUUID();
        Product existing = new Product();
        existing.setId(id);
        Product details = new Product();
        details.setType(ProductType.CLOTHING);
        details.setDescription("desc");
        details.setPrice(java.math.BigDecimal.TEN);
        details.setSize("L");
        details.setImageUrl("img.jpg");
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(existing);
        ProductDTO dto = productService.updateProduct(id, details);
        assertNotNull(dto);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        UUID id = UUID.randomUUID();
        Product details = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(id, details));
    }

    @Test
    void testGetProductsByType_Success() {
        ProductType type = ProductType.CLOTHING;
        Product p = new Product();
        when(productRepository.findByType(type)).thenReturn(java.util.Arrays.asList(p));
        assertEquals(1, productService.getProductsByType(type).size());
    }

    @Test
    void testGetProductsByType_Empty() {
        ProductType type = ProductType.CLOTHING;
        when(productRepository.findByType(type)).thenReturn(java.util.Collections.emptyList());
        assertTrue(productService.getProductsByType(type).isEmpty());
    }
}
