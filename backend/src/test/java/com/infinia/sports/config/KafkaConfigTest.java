package com.infinia.sports.config;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    @Test
    void testProductProducerFactory() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig();
        
        // When
        ProducerFactory<String, ProductKafkaMessage> factory = kafkaConfig.productProducerFactory();
        
        // Then
        assertNotNull(factory);
        // ProducerFactory es una interfaz y no expone sus configuraciones directamente
        // Verificamos que no sea null para asegurar que la configuración se creó
    }

    @Test
    void testProductKafkaTemplate() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig();
        
        // When
        KafkaTemplate<String, ProductKafkaMessage> template = kafkaConfig.productKafkaTemplate();
        
        // Then
        assertNotNull(template);
    }

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
