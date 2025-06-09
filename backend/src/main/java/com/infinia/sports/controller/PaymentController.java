package com.infinia.sports.controller;

import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.dto.RedsysPaymentRequestDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.service.impl.BizumPaymentServiceImpl;
import com.infinia.sports.service.impl.RedsysPaymentServiceImpl;

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
    private final RedsysPaymentServiceImpl redsysPaymentService;

    public PaymentController(BizumPaymentServiceImpl bizumPaymentService, RedsysPaymentServiceImpl redsysPaymentService) {
        this.bizumPaymentService = bizumPaymentService;
        this.redsysPaymentService = redsysPaymentService;
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

    /**
     * Endpoint para procesar pago Redsys
     */
    @PostMapping("/redsys")
    public ResponseEntity<RedsysPaymentResponseDTO> processRedsysPayment(@Valid @RequestBody RedsysPaymentRequestDTO request) {
        logger.info("[Redsys] Petición recibida: {}", request);
        RedsysPaymentResponseDTO response = redsysPaymentService.processRedsysPayment(request);
        logger.info("[Redsys] Respuesta enviada: {}", response);
        return ResponseEntity.ok(response);
    }
}
