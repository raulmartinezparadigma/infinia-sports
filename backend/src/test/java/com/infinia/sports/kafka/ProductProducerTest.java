package com.infinia.sports.kafka;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductProducerTest {

    @Mock
    private KafkaTemplate<String, ProductKafkaMessage> kafkaTemplate;

    @InjectMocks
    private ProductProducer productProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendProduct() {
        // Given
        ProductKafkaMessage message = new ProductKafkaMessage();
        String productId = "123e4567-e89b-12d3-a456-426614174000";
        message.setId(productId);
        message.setDescription("Zapatillas de running");
        message.setType("FOOTWEAR");
        message.setSkuId("SKU123");

        // When
        productProducer.sendProduct(message);

        // Then
        verify(kafkaTemplate).send(eq("products-topic"), eq(productId), eq(message));
    }
}
