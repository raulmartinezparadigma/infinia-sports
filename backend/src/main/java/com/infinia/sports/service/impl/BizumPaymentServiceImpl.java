package com.infinia.sports.service.impl;

import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.Payment;
import com.infinia.sports.repository.mongo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.UUID;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;

/**
 * Servicio mock para pagos Bizum
 */
@Service
public class BizumPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BizumPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;

    public BizumPaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Procesa el pago Bizum de forma simulada
     */
    public BizumPaymentResponseDTO processBizumPayment(BizumPaymentRequestDTO request) {
        // Traza de entrada
        logger.info("[BizumService] DTO recibido: {}", request);
        // Simulación de retardo para Bizum (espera de 5 segundos antes de confirmar)
        try {
            Thread.sleep(5000); // Pausa de 5 segundos para simular el procesamiento real
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Se puede loguear si se desea, pero no se interrumpe el flujo de la simulación
        }
        // Simulación de lógica de pago Bizum con persistencia real
        String transactionId = UUID.randomUUID().toString();
        String providerResponse = "Pago Bizum simulado correctamente";

        // Crear entidad Payment y guardar en MongoDB
        Payment payment = Payment.builder()
                .id(request.getPaymentId())
                .orderId(null) // Se puede asociar a un pedido real si se requiere
                .method(PaymentMethod.BIZUM)
                .status(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("10.00")) // Monto simulado
                .currency("EUR")
                .transactionId(transactionId)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .payerInfo(request.getPhoneNumber())
                .providerResponse(providerResponse)
                .build();

        paymentRepository.save(payment);

        BizumPaymentResponseDTO response = BizumPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .transactionId(transactionId)
                .status(payment.getStatus().name())
                .providerResponse(providerResponse)
                .build();
        // Traza de salida
        logger.info("[BizumService] DTO respuesta: {}", response);
        return response;
    }
}
