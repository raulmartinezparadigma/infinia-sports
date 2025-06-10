package com.infinia.sports.mail;

import jakarta.mail.MessagingException;

/**
 * Servicio para envío de correos de resumen de pedido
 */
public interface OrderMailService {
    /**
     * Envía el resumen del pedido al usuario
     * @param to Email de destino
     * @param subject Asunto del correo
     * @param htmlContent Contenido HTML del correo
     * @throws MessagingException en caso de error de envío
     */
    void sendOrderSummary(String to, String subject, String htmlContent) throws MessagingException;
}
