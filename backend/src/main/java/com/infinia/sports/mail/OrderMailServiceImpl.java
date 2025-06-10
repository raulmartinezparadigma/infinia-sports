package com.infinia.sports.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementación del servicio de envío de correos de resumen de pedido
 */
@Service
public class OrderMailServiceImpl implements OrderMailService {
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
        mailSender.send(message);
    }
}
