package com.infinia.sports.kafka;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio productor para enviar mensajes de productos a Kafka.
 * Solo encapsula el envío al topic correspondiente, sin lógica de negocio.
 */
@Service
public class ProductProducer {
    private static final Logger logger = LoggerFactory.getLogger(ProductProducer.class);
    private static final String PRODUCT_TOPIC = "products-topic";

    private final KafkaTemplate<String, ProductKafkaMessage> kafkaTemplate;

    /**
     * Constructor con inyección de dependencias
     */
    public ProductProducer(KafkaTemplate<String, ProductKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Envía un mensaje de producto al topic de Kafka
     * @param productMessage DTO del producto a enviar
     */
    public void sendProduct(ProductKafkaMessage productMessage) {
        logger.info("Enviando producto a Kafka: {}", productMessage);
        kafkaTemplate.send(PRODUCT_TOPIC, productMessage.getId(), productMessage);
    }
}
