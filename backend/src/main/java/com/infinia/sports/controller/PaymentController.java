package com.infinia.sports.controller;

import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.dto.PaymentInfoDTO;
import com.infinia.sports.model.dto.RedsysPaymentRequestDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.model.dto.TransferPaymentRequestDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import com.infinia.sports.service.PaymentService;
import com.infinia.sports.service.impl.BizumPaymentServiceImpl;
import com.infinia.sports.service.impl.RedsysPaymentServiceImpl;
import com.infinia.sports.service.impl.TransferPaymentServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para endpoints de pagos
 * (Solo Bizum mock por ahora)
 */
@RestController
@RequestMapping("/api/payments")
@Validated
@Tag(name = "payment", description = "API para pagos")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final BizumPaymentServiceImpl bizumPaymentService;
    private final RedsysPaymentServiceImpl redsysPaymentService;
    private final TransferPaymentServiceImpl transferPaymentService;
    private final PaymentService paymentService;

    public PaymentController(BizumPaymentServiceImpl bizumPaymentService, RedsysPaymentServiceImpl redsysPaymentService, TransferPaymentServiceImpl transferPaymentService, PaymentService paymentService) {
        this.bizumPaymentService = bizumPaymentService;
        this.redsysPaymentService = redsysPaymentService;
        this.transferPaymentService = transferPaymentService;
        this.paymentService = paymentService;
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

    /**
     * Endpoint para procesar pago por transferencia bancaria
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransferPaymentResponseDTO> processTransferPayment(@Valid @RequestBody TransferPaymentRequestDTO request) {
        logger.info("[Transfer] Petición recibida: {}", request);
        TransferPaymentResponseDTO response = transferPaymentService.processTransferPayment(request);
        logger.info("[Transfer] Respuesta enviada: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene información de pago de un pedido
     */
    @GetMapping("/order/{orderId}/payment")
    @Operation(summary = "Obtener información de pago", description = "Obtiene información de pago de un pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información de pago obtenida correctamente", 
                    content = @Content(schema = @Schema(implementation = PaymentInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PaymentInfoDTO> getPaymentInfoByOrderId(@PathVariable String orderId) {
        PaymentInfoDTO dto = paymentService.getPaymentInfoByOrderId(orderId);
        return ResponseEntity.ok(dto);
    }
}
