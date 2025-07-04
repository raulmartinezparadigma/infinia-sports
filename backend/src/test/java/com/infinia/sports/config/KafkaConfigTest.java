package com.infinia.sports.config;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaConfigTest {


    @Test
    void testProductConsumerFactory() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig();
        
        // When
        ConsumerFactory<String, ProductKafkaMessage> factory = kafkaConfig.productConsumerFactory();
        
        // Then
        assertNotNull(factory);
        // ConsumerFactory es una interfaz y no expone sus configuraciones directamente
        // Verificamos que no sea null para asegurar que la configuración se creó
    }

    @Test
    void testProductKafkaListenerContainerFactory() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig();
        
        // When
        ConcurrentKafkaListenerContainerFactory<String, ProductKafkaMessage> factory = 
            kafkaConfig.productKafkaListenerContainerFactory();
        
        // Then
        assertNotNull(factory);
    }
}
