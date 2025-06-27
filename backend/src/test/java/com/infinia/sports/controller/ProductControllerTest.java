package com.infinia.sports.controller;

import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductController
 * Se mockean las respuestas del ProductService y se verifica la lógica del controlador
 */
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private ProductDTO productDTO;
    private UUID productId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setSkuId("sku123");
        product.setType(ProductType.SNEAKERS);
        product.setDescription("Zapatillas deportivas");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setSize("42");
        product.setImageUrl("img.jpg");

        productDTO = ProductDTO.builder()
                .id(productId)
                .skuId("sku123")
                .type("SNEAKERS")
                .description("Zapatillas deportivas")
                .price(BigDecimal.valueOf(99.99))
                .size("42")
                .imageUrl("img.jpg")
                .build();
    }

    @Test
    void testGetAllProducts_ReturnsList() {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(productDTO));
        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts(null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_ReturnsProductDTO() {
        when(productService.getProductById(productId)).thenReturn(productDTO);
        ResponseEntity<ProductDTO> response = productController.getProductById(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productService.getProductById(productId)).thenThrow(new com.infinia.sports.exception.ResourceNotFoundException("Not found"));
        ResponseEntity<ProductDTO> response = productController.getProductById(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateProduct_ReturnsCreated() {
        when(productService.saveProduct(product)).thenReturn(productDTO);
        ResponseEntity<ProductDTO> response = productController.createProduct(product);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
        verify(productService, times(1)).saveProduct(product);
    }

    @Test
    void testCreateProduct_Exception() {
        when(productService.saveProduct(product)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<ProductDTO> response = productController.createProduct(product);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateProduct_ReturnsUpdated() {
        when(productService.updateProduct(productId, product)).thenReturn(productDTO);
        ResponseEntity<ProductDTO> response = productController.updateProduct(productId, product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
        verify(productService, times(1)).updateProduct(productId, product);
    }

    @Test
    void testUpdateProduct_Exception() {
        when(productService.updateProduct(productId, product)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<ProductDTO> response = productController.updateProduct(productId, product);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_ReturnsNoContent() {
        doNothing().when(productService).deleteProductById(any(UUID.class));
        ResponseEntity<Void> response = productController.deleteProduct(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProductById(any(UUID.class));
    }

    @Test
    void testDeleteProduct_Exception() {
        doThrow(new com.infinia.sports.exception.ResourceNotFoundException("Not found")).when(productService).deleteProductById(any(UUID.class));
        ResponseEntity<Void> response = productController.deleteProduct(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testImportProducts_ReturnsImportedList() {
        List<Product> products = Arrays.asList(product);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);
        when(productService.importProducts(products)).thenReturn(productDTOs);
        ResponseEntity<List<ProductDTO>> response = productController.importProducts(products);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).importProducts(products);
    }

    @Test
    void testImportProducts_Exception() {
        List<Product> products = Arrays.asList(product);
        when(productService.importProducts(products)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<List<ProductDTO>> response = productController.importProducts(products);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetAllProducts_FilterByType() {
        when(productService.getProductsByType(ProductType.SNEAKERS)).thenReturn(Collections.singletonList(productDTO));
        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts(ProductType.SNEAKERS, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getProductsByType(ProductType.SNEAKERS);
    }

    @Test
    void testGetAllProducts_FilterByDescription() {
        String desc = "deportivas";
        when(productService.getProductsByDescription(desc)).thenReturn(Collections.singletonList(productDTO));
        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts(null, desc, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getProductsByDescription(desc);
    }

    @Test
    void testGetAllProducts_FilterBySize() {
        String size = "42";
        when(productService.getProductsBySize(size)).thenReturn(Collections.singletonList(productDTO));
        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts(null, null, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getProductsBySize(size);
    }
}
