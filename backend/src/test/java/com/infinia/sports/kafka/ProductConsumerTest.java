package com.infinia.sports.kafka;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import com.infinia.sports.model.Product;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductConsumerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductConsumer productConsumer;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private ProductKafkaMessage validMessage;
    private final String validUuid = "123e4567-e89b-12d3-a456-426614174000";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a valid ProductKafkaMessage for testing
        validMessage = new ProductKafkaMessage();
        validMessage.setId(validUuid);
        validMessage.setSkuId("SKU12345678");
        validMessage.setType("CLOTHING");
        validMessage.setDescription("Zapatillas de running Nike Air Max");
        validMessage.setPrice(new BigDecimal("99.99"));
        validMessage.setSize("42");
        validMessage.setImageUrl("https://example.com/image.jpg");
    }

    @Test
    void testConsumeProduct_NewProduct_Success() {
        // Given
        when(productRepository.existsById(any(UUID.class))).thenReturn(false);
        when(productRepository.findBySkuId(anyString())).thenReturn(Optional.empty());

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productService).createProductFromKafka(validMessage);
    }

    @Test
    void testConsumeProduct_ExistingProductById_NoSave() {
        // Given
        when(productRepository.existsById(any(UUID.class))).thenReturn(true);

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConsumeProduct_ExistingProductBySkuId_NoSave() {
        // Given
        when(productRepository.existsById(any(UUID.class))).thenReturn(false);
        when(productRepository.findBySkuId(anyString())).thenReturn(Optional.of(new Product()));

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConsumeProduct_NullId_NoSave() {
        // Given
        validMessage.setId(null);

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConsumeProduct_EmptyId_NoSave() {
        // Given
        validMessage.setId("");

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConsumeProduct_NullPrice_UsesZero() {
        // Given
        when(productRepository.existsById(any(UUID.class))).thenReturn(false);
        when(productRepository.findBySkuId(anyString())).thenReturn(Optional.empty());
        validMessage.setPrice(null);

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productService).createProductFromKafka(validMessage);

    }

    @Test
    void testConsumeProduct_InvalidUUID_HandlesException() {
        // Given
        validMessage.setId("invalid-uuid");

        // When
        productConsumer.consumeProduct(validMessage);

        // Then
        verify(productRepository, never()).save(any(Product.class));
    }
}
