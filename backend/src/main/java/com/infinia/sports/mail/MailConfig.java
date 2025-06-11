package com.infinia.sports.mail;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Configuración del cliente JavaMailSender para envío de correos vía SMTP (Gmail)
 */
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("infiniasports@gmail.com"); // Cambiar por el real o usar variable de entorno
        mailSender.setPassword("netm bksf qffg pbry"); // Contraseña de aplicación de Google

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.connectiontimeout", "15000"); // Aumentado a 15 segundos
        props.put("mail.smtp.timeout", "15000"); // Aumentado a 15 segundos
        props.put("mail.smtp.writetimeout", "15000"); // Aumentado a 15 segundos
        return mailSender;
    }
}
