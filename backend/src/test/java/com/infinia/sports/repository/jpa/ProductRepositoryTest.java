package com.infinia.sports.repository.jpa;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product sneakerProduct;
    private Product clothingProduct;
    private Product supplementProduct;

    @BeforeEach
    void setUp() {
        // Create test products
        sneakerProduct = new Product();
        sneakerProduct.setSkuId("123456789012345678");
        sneakerProduct.setId(UUID.randomUUID());
        sneakerProduct.setType(ProductType.SNEAKERS);
        sneakerProduct.setDescription("Running Shoes XZ500");
        sneakerProduct.setPrice(new BigDecimal("99.99"));
        sneakerProduct.setSize("42");
        sneakerProduct.setImageUrl("http://example.com/shoes.jpg");

        clothingProduct = new Product();
        clothingProduct.setSkuId("223456789012345678");
        clothingProduct.setId(UUID.randomUUID());
        clothingProduct.setType(ProductType.CLOTHING);
        clothingProduct.setDescription("Sports T-Shirt");
        clothingProduct.setPrice(new BigDecimal("29.99"));
        clothingProduct.setSize("M");
        clothingProduct.setImageUrl("http://example.com/tshirt.jpg");

        supplementProduct = new Product();
        supplementProduct.setSkuId("323456789012345678");
        supplementProduct.setId(UUID.randomUUID());
        supplementProduct.setType(ProductType.SUPPLEMENT);
        supplementProduct.setDescription("Protein Powder");
        supplementProduct.setPrice(new BigDecimal("39.99"));
        supplementProduct.setSize("500g");
        supplementProduct.setImageUrl("http://example.com/protein.jpg");

        // Save the products
        productRepository.save(sneakerProduct);
        productRepository.save(clothingProduct);
        productRepository.save(supplementProduct);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testFindBySkuId() {
        // When
        Optional<Product> foundProduct = productRepository.findBySkuId(sneakerProduct.getSkuId());

        // Then
        assertTrue(foundProduct.isPresent());
        assertEquals(sneakerProduct.getDescription(), foundProduct.get().getDescription());
        assertEquals(ProductType.SNEAKERS, foundProduct.get().getType());
        
        // Test with non-existent SKU
        Optional<Product> notFoundProduct = productRepository.findBySkuId("999999999999999999");
        assertFalse(notFoundProduct.isPresent());
    }

    @Test
    void testFindByType() {
        // When
        List<Product> sneakers = productRepository.findByType(ProductType.SNEAKERS);
        List<Product> clothing = productRepository.findByType(ProductType.CLOTHING);
        List<Product> supplements = productRepository.findByType(ProductType.SUPPLEMENT);

        // Then
        assertEquals(1, sneakers.size());
        assertEquals(sneakerProduct.getSkuId(), sneakers.get(0).getSkuId());
        
        assertEquals(1, clothing.size());
        assertEquals(clothingProduct.getSkuId(), clothing.get(0).getSkuId());
        
        assertEquals(1, supplements.size());
        assertEquals(supplementProduct.getSkuId(), supplements.get(0).getSkuId());
    }

    @Test
    void testFindByDescriptionContainingIgnoreCase() {
        // When - search using lowercase
        List<Product> runningProducts = productRepository.findByDescriptionContainingIgnoreCase("running");
        
        // Then
        assertEquals(1, runningProducts.size());
        assertEquals(sneakerProduct.getSkuId(), runningProducts.get(0).getSkuId());
        
        // When - search using mixed case
        List<Product> proteinProducts = productRepository.findByDescriptionContainingIgnoreCase("PrOtEiN");
        
        // Then
        assertEquals(1, proteinProducts.size());
        assertEquals(supplementProduct.getSkuId(), proteinProducts.get(0).getSkuId());
        
        // When - search with no results
        List<Product> noProducts = productRepository.findByDescriptionContainingIgnoreCase("basketball");
        
        // Then
        assertTrue(noProducts.isEmpty());
    }
    
    @Test
    void testFindBySize() {
        // When
        List<Product> medium = productRepository.findBySize("M");
        
        // Then
        assertEquals(1, medium.size());
        assertEquals(clothingProduct.getSkuId(), medium.get(0).getSkuId());
        
        // When - no matches
        List<Product> small = productRepository.findBySize("S");
        
        // Then
        assertTrue(small.isEmpty());
    }
}
