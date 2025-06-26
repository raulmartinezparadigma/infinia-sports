package com.infinia.sports.service;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.BizumPaymentRequestDTO;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.model.Order;
import com.infinia.sports.service.impl.BizumPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BizumPaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMailPaymentService orderMailPaymentService;
    @InjectMocks
    private BizumPaymentServiceImpl bizumPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bizumPaymentService = new BizumPaymentServiceImpl(paymentRepository, cartRepository, orderRepository, orderMailPaymentService);
    }

    @Test
    void testProcessBizumPayment_sendsMailAndSavesPayment() {
        BizumPaymentRequestDTO request = new BizumPaymentRequestDTO();
        request.setOrderId("order789");
        request.setPaymentId("payment789");
        Payment payment = Payment.builder()
                .orderId("order789")
                .method(PaymentMethod.BIZUM)
                .status(PaymentStatus.COMPLETED)
                .build();
        payment.setId("payment789");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Order order = new Order();
        order.setOrderId("order789");
        when(orderRepository.findByOrderId("order789")).thenReturn(java.util.Optional.of(order));

        BizumPaymentResponseDTO dto = bizumPaymentService.processBizumPayment(request);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderMailPaymentService, atLeastOnce()).sendOrderConfirmationEmail("order789");
        assertNotNull(dto);
        assertEquals("payment789", dto.getPaymentId());
    }
}
