package com.infinia.sports.service;

public interface OrderMailPaymentService {
    /**
     * Envía el correo de resumen de pedido tras pago exitoso.
     * @param orderId ID del pedido
     */
    void sendOrderConfirmationEmail(String orderId);
}
