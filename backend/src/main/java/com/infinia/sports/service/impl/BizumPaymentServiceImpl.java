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
import com.infinia.sports.model.Order;
import com.infinia.sports.repository.mongo.OrderRepository;


/**
 * Servicio mock para pagos Bizum
 */
@Service
public class BizumPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BizumPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final com.infinia.sports.repository.mongo.CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public BizumPaymentServiceImpl(PaymentRepository paymentRepository, com.infinia.sports.repository.mongo.CartRepository cartRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Procesa el pago Bizum de forma simulada
     */
    public BizumPaymentResponseDTO processBizumPayment(BizumPaymentRequestDTO request) {
        // Traza de entrada
        logger.info("[BizumService] DTO recibido: {}", request);
        logger.info("[BizumService] orderId recibido en DTO: {}", request.getOrderId());
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
                .orderId(request.getOrderId())
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

        logger.info("[BizumService] orderId guardado en Payment: {}", payment.getOrderId());
        paymentRepository.save(payment);

        // Si el pago es COMPLETED y tiene orderId, actualiza el estado de la orden a COMPLETED
        if (payment.getStatus() == PaymentStatus.COMPLETED && payment.getOrderId() != null && !payment.getOrderId().isEmpty()) {
            logger.info("[BizumService] Buscando orden con orderId: {} en OrderRepository", payment.getOrderId());
            try {
                java.util.Optional<Order> optOrder = orderRepository.findByOrderId(payment.getOrderId());
                if (optOrder.isPresent()) {
                    Order order = optOrder.get();
                    logger.info("[BizumService] Orden encontrada: orderId={}, id={}, status antes='{}'", order.getOrderId(), order.getId(), order.getStatus());
                    order.setStatus("COMPLETED");
                    orderRepository.save(order);
                    logger.info("[BizumService] Estado de la orden actualizado a COMPLETED para orderId={}, status después='{}'", payment.getOrderId(), order.getStatus());
                } else {
                    logger.warn("[BizumService] No se encontró la orden para orderId={} al intentar actualizar estado tras pago Bizum", payment.getOrderId());
                }
            } catch (Exception e) {
                logger.error("[BizumService] Error al actualizar estado de la orden tras pago Bizum: {}", e.getMessage(), e);
            }
        }

        // Vaciar el carrito tras el pago
        try {
            if (request.getUserId() != null && !request.getUserId().isEmpty()) {
                logger.info("[BizumService] Eliminando carrito por userId: {}", request.getUserId());
                cartRepository.deleteByUserId(request.getUserId());
            } else {
                logger.info("[BizumService] Eliminando carrito por sessionId (simulado con paymentId): {}", request.getPaymentId());
                cartRepository.deleteBySessionId(request.getPaymentId());
            }
            logger.info("[BizumService] Carrito eliminado correctamente tras pago Bizum");
        } catch (Exception e) {
            logger.error("[BizumService] Error al eliminar el carrito tras pago Bizum: {}", e.getMessage(), e);
        }

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
