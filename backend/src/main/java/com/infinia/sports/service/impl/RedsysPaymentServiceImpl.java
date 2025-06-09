package com.infinia.sports.service.impl;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.dto.RedsysPaymentRequestDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.repository.mongo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio simulado para pagos Redsys
 */
@Service
@RequiredArgsConstructor
public class RedsysPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(RedsysPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    /**
     * Procesa el pago Redsys de forma simulada
     */
    public RedsysPaymentResponseDTO processRedsysPayment(RedsysPaymentRequestDTO request) {
        // Traza de entrada
        logger.info("[RedsysService] DTO recibido: {}", request);
        logger.info("[RedsysService] orderId recibido en DTO: {}", request.getOrderId());
        // Simulación de retardo para Redsys (espera de 3 segundos antes de confirmar)
        try {
            Thread.sleep(3000); // Pausa de 3 segundos para simular el procesamiento real
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Simulación de lógica Redsys
        String transactionId = "REDSYS-" + System.currentTimeMillis();
        String providerResponse = "Pago Redsys simulado OK";
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .method(com.infinia.sports.model.PaymentMethod.REDSYS)
                .status(com.infinia.sports.model.PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .amount(request.getAmount())
                .currency("EUR")
                .providerResponse(providerResponse)
                .build();
        paymentRepository.save(payment);
        // Actualizar estado del pedido a COMPLETED si existe
        orderRepository.findByOrderId(request.getOrderId()).ifPresent(order -> {
            order.setStatus("COMPLETED");
            orderRepository.save(order);
            logger.info("[RedsysService] Order actualizado a COMPLETED: {}", order.getOrderId());
        });
        // Eliminar carrito tras pago exitoso
        try {
            cartRepository.deleteById(request.getOrderId());
            logger.info("[RedsysService] Carrito eliminado correctamente tras pago Redsys");
        } catch (Exception e) {
            logger.error("[RedsysService] Error al eliminar el carrito tras pago Redsys: {}", e.getMessage(), e);
        }
        RedsysPaymentResponseDTO response = RedsysPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .transactionId(transactionId)
                .status(payment.getStatus().name())
                .providerResponse(providerResponse)
                .build();
        logger.info("[RedsysService] DTO respuesta: {}", response);
        return response;
    }
}
