package com.infinia.sports.service;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.RedsysPaymentRequestDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.model.Order;
import com.infinia.sports.service.impl.RedsysPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RedsysPaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMailPaymentService orderMailPaymentService;
    @InjectMocks
    private RedsysPaymentServiceImpl redsysPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redsysPaymentService = new RedsysPaymentServiceImpl(paymentRepository, cartRepository, orderRepository, orderMailPaymentService);
    }

    @Test
    void testProcessRedsysPayment_sendsMailAndSavesPayment() {
        RedsysPaymentRequestDTO request = new RedsysPaymentRequestDTO();
        request.setOrderId("order456");
        request.setAmount(new java.math.BigDecimal("99.99"));
        Payment payment = Payment.builder()
                .orderId("order456")
                .amount(request.getAmount())
                .method(PaymentMethod.REDSYS)
                .status(PaymentStatus.COMPLETED)
                .build();
        payment.setId("paymentId");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Order order = new Order();
        order.setOrderId("order456");
        when(orderRepository.findByOrderId("order456")).thenReturn(java.util.Optional.of(order));

        RedsysPaymentResponseDTO dto = redsysPaymentService.processRedsysPayment(request);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderMailPaymentService, atLeastOnce()).sendOrderConfirmationEmail("order456");
        assertNotNull(dto);
        assertEquals("order456", request.getOrderId());
        assertEquals("paymentId", dto.getPaymentId()); 
    }
}
