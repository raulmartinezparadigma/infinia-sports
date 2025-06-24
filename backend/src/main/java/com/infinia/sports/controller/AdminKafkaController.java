package com.infinia.sports.controller;

import com.infinia.sports.kafka.ProductProducer;
import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/kafka")
@io.swagger.v3.oas.annotations.tags.Tag(name = "admin-kafka", description = "API de administración de Kafka")
public class AdminKafkaController {
    private final ProductProducer productProducer;

    public AdminKafkaController(ProductProducer productProducer) {
        this.productProducer = productProducer;
    }

    /**
     * Permite dar de alta manualmente un producto en la cola Kafka a partir de un JSON recibido.
     */
    @PostMapping("/product")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendProductToKafka(@RequestBody ProductKafkaMessage productKafkaMessage) {
        try {
            // Validación básica (puedes ampliar con validaciones de negocio)
            if (productKafkaMessage.getSkuId() == null || productKafkaMessage.getSkuId().isBlank()) {
                return ResponseEntity.badRequest().body("El campo skuId es obligatorio");
            }
            productProducer.sendProduct(productKafkaMessage);
            return ResponseEntity.ok().body("Producto enviado correctamente a Kafka");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al enviar producto a Kafka: " + e.getMessage());
        }
    }
}
