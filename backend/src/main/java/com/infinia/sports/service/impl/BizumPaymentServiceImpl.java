package com.infinia.sports.service.impl;

import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
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
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.service.OrderMailPaymentService;
import com.infinia.sports.mapper.mapstruct.PaymentMapperMS;

/**
 * Servicio mock para pagos Bizum
 */
@Service
public class BizumPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BizumPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderMailPaymentService orderMailPaymentService;
    private final PaymentMapperMS paymentMapper;

    public BizumPaymentServiceImpl(PaymentRepository paymentRepository, CartRepository cartRepository, 
                                   OrderRepository orderRepository, OrderMailPaymentService orderMailPaymentService,
                                   PaymentMapperMS paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderMailPaymentService = orderMailPaymentService;
        this.paymentMapper = paymentMapper;
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
                // Si hay varias órdenes por error histórico, actualiza todas para mantener consistencia
                java.util.List<Order> orders = orderRepository.findByOrderId(payment.getOrderId()).stream().toList();
                if (!orders.isEmpty()) {
                    for (Order order : orders) {
                        logger.info("[BizumService] Orden encontrada: orderId={}, id={}, status antes='{}'", order.getOrderId(), order.getId(), order.getStatus());
                        order.setStatus(payment.getStatus().name()); // Usar el status EXACTO del Payment (mayúsculas)
                        orderRepository.save(order);
                        logger.info("[BizumService] Estado de la orden actualizado a {} para orderId={}, status después='{}'", payment.getStatus().name(), payment.getOrderId(), order.getStatus());
                        // Enviar email de resumen de pedido tras pago exitoso (centralizado)
                        orderMailPaymentService.sendOrderConfirmationEmail(order.getOrderId());
                    }
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

        // Traza de salida
        BizumPaymentResponseDTO dto = paymentMapper.toBizumPaymentResponseDTO(payment);
        logger.info("[BizumService] DTO devuelto: {}", dto);
        return dto;
    }
}
