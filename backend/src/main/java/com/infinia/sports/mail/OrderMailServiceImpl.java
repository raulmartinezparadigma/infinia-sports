package com.infinia.sports.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl; // Importar JavaMailSenderImpl

/**
 * Implementación del servicio de envío de correos de resumen de pedido
 */
@Service
public class OrderMailServiceImpl implements OrderMailService {
    private static final Logger logger = LoggerFactory.getLogger(OrderMailServiceImpl.class); // Añadir logger
    private final JavaMailSender mailSender;

    public OrderMailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOrderSummary(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true para HTML

        // --- INICIO LOGGING TEMPORAL DE CREDENCIALES (ELIMINAR DESPUÉS) ---
        if (mailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
            logger.info("[OrderMailService] Intentando enviar email con Username: {}", senderImpl.getUsername());
            // NO LOGUEAR PASSWORD EN PRODUCCIÓN O ENTORNOS COMPARTIDOS.
            // ESTO ES SOLO PARA DEPURACIÓN LOCAL Y DEBE SER ELIMINADO.
            // logger.info("[OrderMailService] Password configurado: {}", senderImpl.getPassword()); 
            logger.warn("[OrderMailService] IMPORTANTE: El logging de username está activo. Si descomentaste el log de password, elimínalo inmediatamente después de la prueba.");
        } else {
            logger.warn("[OrderMailService] No se pudo obtener el username/password del mailSender, no es una instancia de JavaMailSenderImpl.");
        }
        // --- FIN LOGGING TEMPORAL DE CREDENCIALES ---

        mailSender.send(message);
    }
}
