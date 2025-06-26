package com.infinia.sports.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.infinia.sports.mail.OrderMailService;
import com.infinia.sports.model.Order;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.impl.OrderMailPaymentServiceImpl;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderMailPaymentServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMailService mailSenderService;
    @InjectMocks
    private OrderMailPaymentServiceImpl orderMailServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderMailServiceImpl = new OrderMailPaymentServiceImpl(orderRepository, mailSenderService);
    }

    @Test
    void testSendOrderConfirmationEmail_success() throws Exception {
        Order order = new Order();
        order.setOrderId("test123");
        order.setEmail("test@infinia.com");
        when(orderRepository.findByOrderId("test123")).thenReturn(Optional.of(order));

        orderMailServiceImpl.sendOrderConfirmationEmail("test123");

        verify(mailSenderService, times(1)).sendOrderSummary(eq("test@infinia.com"), contains("Infinia Sports"), anyString());
    }

    @Test
    void testSendOrderConfirmationEmail_orderNotFound() throws Exception {
        when(orderRepository.findByOrderId("notfound")).thenReturn(Optional.empty());
        orderMailServiceImpl.sendOrderConfirmationEmail("notfound");
        verify(mailSenderService, never()).sendOrderSummary(anyString(), anyString(), anyString());
    }
}
