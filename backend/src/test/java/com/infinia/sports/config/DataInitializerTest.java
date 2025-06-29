package com.infinia.sports.config;

import com.infinia.sports.kafka.ProductProducer;
import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @InjectMocks
    private DataInitializer dataInitializer;

    @Mock
    private ProductProducer productProducer;

    @Captor
    private ArgumentCaptor<ProductKafkaMessage> productMessageCaptor;

    @Test
    void testLoadData() throws Exception {
        // When
        CommandLineRunner runner = dataInitializer.loadData();
        assertNotNull(runner, "CommandLineRunner should not be null");
        
        // Execute the runner
        runner.run();
        
        // Then
        // Verify products were sent to Kafka
        // We expect 20 products to be sent based on the implementation
        verify(productProducer, times(20)).sendProduct(any(ProductKafkaMessage.class));
    }
    
    @Test
    void testProductMessageFormat() throws Exception {
        // Given
        CommandLineRunner runner = dataInitializer.loadData();
        
        // When
        runner.run();
        
        // Then
        verify(productProducer, atLeast(1)).sendProduct(productMessageCaptor.capture());
        
        ProductKafkaMessage capturedMessage = productMessageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertNotNull(capturedMessage.getId());
        assertNotNull(capturedMessage.getSkuId());
        assertNotNull(capturedMessage.getType());
        assertNotNull(capturedMessage.getDescription());
        assertNotNull(capturedMessage.getPrice());
        assertNotNull(capturedMessage.getSize());
        assertNotNull(capturedMessage.getImageUrl());
    }
}
