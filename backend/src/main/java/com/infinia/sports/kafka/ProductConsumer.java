package com.infinia.sports.kafka;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import com.infinia.sports.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumidor Kafka para productos. Persiste los productos recibidos en la base de datos PostgreSQL.
 */
@Service
@Profile("!e2e-test")
public class ProductConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ProductConsumer.class);
    private final ProductService productService;

    /**
     * Constructor con inyección de dependencias
     */
    public ProductConsumer(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Escucha el topic de productos y persiste cada mensaje recibido
     * @param message DTO del producto recibido por Kafka
     */
    @KafkaListener(topics = "products-topic", groupId = "product-consumer-group", containerFactory = "productKafkaListenerContainerFactory")
    public void consumeProduct(ProductKafkaMessage message) {
        try {
            logger.info("Recibido producto de Kafka: {}. Delegando a ProductService.", message.getDescription());
            productService.createProductFromKafka(message);
        } catch (Exception e) {
            logger.error("Error inesperado en el consumidor de Kafka al procesar el producto: {}", message.getDescription(), e);
            // No relanzamos la excepción para evitar que el listener se bloquee y reintente indefinidamente
        }
    }
}
