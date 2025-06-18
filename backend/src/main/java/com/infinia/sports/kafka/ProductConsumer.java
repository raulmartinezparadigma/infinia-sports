package com.infinia.sports.kafka;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.repository.jpa.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Consumidor Kafka para productos. Persiste los productos recibidos en la base de datos PostgreSQL.
 */
@Service
public class ProductConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ProductConsumer.class);
    private final ProductRepository productRepository;

    /**
     * Constructor con inyección de dependencias
     */
    public ProductConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Escucha el topic de productos y persiste cada mensaje recibido
     * @param message DTO del producto recibido por Kafka
     */
    @KafkaListener(topics = "products-topic", groupId = "product-consumer-group", containerFactory = "productKafkaListenerContainerFactory")
    public void consumeProduct(ProductKafkaMessage message) {
        try {
            logger.info("Recibido producto de Kafka: {}", message);
            // Validación defensiva: ignorar mensajes sin ID
            if (message.getId() == null || message.getId().isBlank()) {
                logger.error("Producto recibido sin ID. Ignorando mensaje: {}", message);
                return;
            }
            // Conversión segura de String a UUID
            java.util.UUID uuid = java.util.UUID.fromString(message.getId());
            // Control idempotente: solo guardar si no existe
            if (!productRepository.existsById(uuid)) {
                Product product = new Product();
                product.setId(uuid);
                product.setType(ProductType.valueOf(message.getType().toUpperCase()));
                product.setDescription(message.getDescription());
                product.setPrice(message.getPrice() != null ? message.getPrice() : BigDecimal.ZERO);
                product.setSize(message.getSize());
                product.setImageUrl(message.getImageUrl());
                productRepository.save(product);
                logger.info("Producto guardado en base de datos: {}", product.getId());
            } else {
                logger.info("Producto con UUID {} ya existe. No se inserta duplicado.", uuid);
            }
        } catch (Exception e) {
            logger.error("Error al procesar producto de Kafka: {}", message, e);
        }
    }
}
