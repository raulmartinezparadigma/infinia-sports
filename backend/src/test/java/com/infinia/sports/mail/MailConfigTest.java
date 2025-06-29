package com.infinia.sports.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MailConfigTest {

    private MailConfig mailConfig;

    @BeforeEach
    void setUp() {
        mailConfig = new MailConfig();
        
        // Configurar propiedades mínimas requeridas
        ReflectionTestUtils.setField(mailConfig, "host", "smtp.example.com");
        ReflectionTestUtils.setField(mailConfig, "port", 587);
        ReflectionTestUtils.setField(mailConfig, "username", "test@example.com");
        ReflectionTestUtils.setField(mailConfig, "password", "password123");
        
        // Configurar propiedades con valores por defecto
        ReflectionTestUtils.setField(mailConfig, "protocol", "smtp");
        ReflectionTestUtils.setField(mailConfig, "smtpAuth", "true");
        ReflectionTestUtils.setField(mailConfig, "starttlsEnable", "true");
        ReflectionTestUtils.setField(mailConfig, "mailSessionDebug", "false");
        ReflectionTestUtils.setField(mailConfig, "connectionTimeout", "15000");
        ReflectionTestUtils.setField(mailConfig, "smtpTimeout", "15000");
        ReflectionTestUtils.setField(mailConfig, "writeTimeout", "15000");
    }

    @Test
    void javaMailSender_ShouldReturnConfiguredInstance() {
        // Act
        JavaMailSender mailSender = mailConfig.javaMailSender();

        // Assert
        assertNotNull(mailSender, "El JavaMailSender no debe ser nulo");
    }
}
