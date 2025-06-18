package com.infinia.sports.config;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración centralizada de Kafka para la carga asíncrona de productos.
 * Separa la configuración de productor y consumidor, utilizando JSON para los mensajes de productos.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    // Configuración del productor Kafka para ProductKafkaMessage
    @Bean
    public ProducerFactory<String, ProductKafkaMessage> productProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Ajustar en producción
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ProductKafkaMessage> productKafkaTemplate() {
        return new KafkaTemplate<>(productProducerFactory());
    }

    // Configuración del consumidor Kafka para ProductKafkaMessage
    @Bean
    public ConsumerFactory<String, ProductKafkaMessage> productConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Ajustar en producción
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "product-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.infinia.sports.kafka.dto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.infinia.sports.kafka.dto.ProductKafkaMessage");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ProductKafkaMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductKafkaMessage> productKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductKafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());
        return factory;
    }
}
