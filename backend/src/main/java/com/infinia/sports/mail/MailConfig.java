package com.infinia.sports.mail;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value; // Asegúrate de que esta importación esté presente
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuración del cliente JavaMailSender para envío de correos vía SMTP.
 * Lee la configuración desde application.properties.
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.protocol:smtp}") // Valor por defecto 'smtp' si no se especifica
    private String protocol;

    @Value("${spring.mail.properties.mail.smtp.auth:true}") // Valor por defecto 'true'
    private String smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") // Valor por defecto 'true'
    private String starttlsEnable;

    @Value("${spring.mail.properties.mail.debug:false}") // Valor por defecto 'false'
    private String mailSessionDebug; // Usado para la propiedad "mail.debug" de JavaMail

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout:15000}") // Valor por defecto '15000'
    private String connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.timeout:15000}") // Valor por defecto '15000'
    private String smtpTimeout;

    @Value("${spring.mail.properties.mail.smtp.writetimeout:15000}") // Valor por defecto '15000'
    private String writeTimeout;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.debug", mailSessionDebug); // Controla la depuración a nivel de propiedades de JavaMail
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        props.put("mail.smtp.timeout", smtpTimeout);
        props.put("mail.smtp.writetimeout", writeTimeout);
        
        return mailSender;
    }
}
