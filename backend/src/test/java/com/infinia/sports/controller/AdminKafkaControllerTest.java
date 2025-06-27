package com.infinia.sports.controller;

import com.infinia.sports.kafka.ProductProducer;
import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminKafkaControllerTest {
    @Mock
    private ProductProducer productProducer;

    @InjectMocks
    private AdminKafkaController adminKafkaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendProductToKafka_ReturnsOk_WhenValidProduct() {
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId("sku123");
        doNothing().when(productProducer).sendProduct(message);
        ResponseEntity<?> response = adminKafkaController.sendProductToKafka(message);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Producto enviado correctamente a Kafka", response.getBody());
    }

    @Test
    void sendProductToKafka_ReturnsBadRequest_WhenSkuIdIsNull() {
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId(null);
        ResponseEntity<?> response = adminKafkaController.sendProductToKafka(message);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("El campo skuId es obligatorio", response.getBody());
    }

    @Test
    void sendProductToKafka_ReturnsBadRequest_WhenSkuIdIsBlank() {
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId("");
        ResponseEntity<?> response = adminKafkaController.sendProductToKafka(message);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("El campo skuId es obligatorio", response.getBody());
    }

    @Test
    void sendProductToKafka_ReturnsInternalServerError_OnException() {
        ProductKafkaMessage message = new ProductKafkaMessage();
        message.setSkuId("sku123");
        doThrow(new RuntimeException("Kafka error")).when(productProducer).sendProduct(message);
        ResponseEntity<?> response = adminKafkaController.sendProductToKafka(message);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al enviar producto a Kafka: Kafka error"));
    }
}
