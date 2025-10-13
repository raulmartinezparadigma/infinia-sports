package com.infinia.sports.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infinia.sports.mail.OrderMailService;
import com.infinia.sports.mail.OrderMailTemplateUtil;
import com.infinia.sports.model.Order;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.OrderMailPaymentService;

@Service
public class OrderMailPaymentServiceImpl implements OrderMailPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(OrderMailPaymentServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMailService orderMailService;

    @Autowired
    public OrderMailPaymentServiceImpl(OrderRepository orderRepository, OrderMailService orderMailService) {
        this.orderRepository = orderRepository;
        this.orderMailService = orderMailService;
    }

    /**
     * Envía email de confirmación de pedido con protección de Circuit Breaker y Retry.
     * 
     * Resilience4j:
     * - Circuit Breaker: Protege contra fallos del servicio de email (SendGrid)
     * - Retry: Reintenta hasta 2 veces con espera exponencial
     * - Fallback: Si falla, registra para envío manual posterior
     * 
     * @param orderId ID del pedido para enviar confirmación
     */
    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmail")
    @Retry(name = "emailService")
    public void sendOrderConfirmationEmail(String orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new com.infinia.sports.exception.ResourceNotFoundException("Pedido no encontrado para envío de email"));
            String html = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            String subject = "Resumen de tu pedido Infinia Sports #" + order.getOrderId();
            String to = order.getEmail();
            orderMailService.sendOrderSummary(to, subject, html);
            logger.info("[OrderMailService] ✅ Email de resumen de pedido enviado a {} para orderId={}", to, orderId);
        } catch (Exception e) {
            logger.error("[OrderMailService] ❌ Error al generar/enviar email de pedido: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar email", e); // Re-lanzar para que Retry/Circuit Breaker lo gestionen
        }
    }

    /**
     * Fallback method cuando el servicio de email falla.
     * Registra el fallo para procesamiento manual posterior.
     * 
     * @param orderId ID del pedido
     * @param ex Excepción que causó el fallo
     */
    private void fallbackSendEmail(String orderId, Exception ex) {
        logger.warn("[OrderMailService] ⚠️ FALLBACK: Servicio de email no disponible para orderId={}. " +
                    "Email debe enviarse manualmente. Causa: {}", 
                    orderId, ex.getMessage());
        
        // TODO: Implementar cola de emails pendientes para reintento posterior
        // Por ahora, solo registramos el fallo para revisión manual
        logger.error("[OrderMailService] 📧 EMAIL PENDIENTE: Revisar y enviar manualmente confirmación para orderId={}", orderId);
    }
}
