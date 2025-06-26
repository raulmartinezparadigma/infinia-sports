package com.infinia.sports.service.impl;

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

    @Override
    public void sendOrderConfirmationEmail(String orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new com.infinia.sports.exception.ResourceNotFoundException("Pedido no encontrado para envío de email"));
            String html = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            String subject = "Resumen de tu pedido Infinia Sports #" + order.getOrderId();
            String to = order.getEmail();
            orderMailService.sendOrderSummary(to, subject, html);
            logger.info("[OrderMailService] Email de resumen de pedido enviado a {} para orderId={}", to, orderId);
        } catch (Exception e) {
            logger.error("[OrderMailService] Error inesperado al generar/enviar email de pedido: {}", e.getMessage(), e);
        }
    }
}
