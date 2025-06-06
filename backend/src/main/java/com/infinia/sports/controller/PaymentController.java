package com.infinia.sports.controller;

import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.service.impl.BizumPaymentServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador para endpoints de pagos
 * (Solo Bizum mock por ahora)
 */
@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PaymentController.class);
    private final BizumPaymentServiceImpl bizumPaymentService;

    public PaymentController(BizumPaymentServiceImpl bizumPaymentService) {
        this.bizumPaymentService = bizumPaymentService;
    }

    /**
     * Endpoint para procesar pago Bizum
     */
    @PostMapping("/bizum")
    public ResponseEntity<BizumPaymentResponseDTO> processBizumPayment(@Valid @RequestBody BizumPaymentRequestDTO request) {
        // Traza de entrada
        logger.info("[Bizum] Petición recibida: {}", request);
        BizumPaymentResponseDTO response = bizumPaymentService.processBizumPayment(request);
        // Traza de salida
        logger.info("[Bizum] Respuesta enviada: {}", response);
        return ResponseEntity.ok(response);
    }
}
