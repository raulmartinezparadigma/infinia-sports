package com.infinia.sports.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderMailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @InjectMocks
    private OrderMailServiceImpl orderMailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendOrderSummary_Success() throws MessagingException {
        // Given
        String to = "customer@example.com";
        String subject = "Resumen de su pedido";
        String htmlContent = "<h1>Detalles del pedido</h1><p>Producto: Zapatillas Nike</p>";
        
        // When
        orderMailService.sendOrderSummary(to, subject, htmlContent);
        
        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessageCaptor.capture());
        
        assertEquals(mimeMessage, mimeMessageCaptor.getValue());
    }
    
    @Test
    void testSendOrderSummary_WithJavaMailSenderImpl() throws MessagingException {
        // Given
        JavaMailSenderImpl javaMailSenderImpl = mock(JavaMailSenderImpl.class);
        when(javaMailSenderImpl.createMimeMessage()).thenReturn(mimeMessage);
        when(javaMailSenderImpl.getUsername()).thenReturn("test@infiniasports.com");
        
        OrderMailServiceImpl serviceWithImpl = new OrderMailServiceImpl(javaMailSenderImpl);
        
        String to = "customer@example.com";
        String subject = "Resumen de su pedido";
        String htmlContent = "<h1>Detalles del pedido</h1><p>Producto: Zapatillas Nike</p>";
        
        // When
        serviceWithImpl.sendOrderSummary(to, subject, htmlContent);
        
        // Then
        verify(javaMailSenderImpl).createMimeMessage();
        verify(javaMailSenderImpl).send(any(MimeMessage.class));
        verify(javaMailSenderImpl).getUsername();
    }
    
    @Test
    void testSendOrderSummary_ThrowsMessagingException()  {
        // Given
        String to = "customer@example.com";
        String subject = "Resumen de su pedido";
        String htmlContent = "<html><body><h1>Resumen de Pedido</h1></body></html>";
        
        doThrow(new MailSendException("Error sending email")).when(mailSender).send(any(MimeMessage.class));
        
        // When/Then
        assertThrows(MailSendException.class, () -> {
            orderMailService.sendOrderSummary(to, subject, htmlContent);
        });
    }
}
