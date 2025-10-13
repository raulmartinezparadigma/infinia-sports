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
import com.infinia.sports.mapper.mapstruct.PaymentMapperMS;
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
    @Mock
    private PaymentMapperMS paymentMapper;
    @InjectMocks
    private RedsysPaymentServiceImpl redsysPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redsysPaymentService = new RedsysPaymentServiceImpl(paymentRepository, cartRepository, orderRepository, orderMailPaymentService, paymentMapper);
        
        // Configurar mock por defecto para paymentMapper
        when(paymentMapper.toRedsysPaymentResponseDTO(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            RedsysPaymentResponseDTO dto = new RedsysPaymentResponseDTO();
            dto.setPaymentId(payment.getId());
            dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
            return dto;
        });
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

    @Test
    void testProcessRedsysPayment_paymentSaveThrows() {
        RedsysPaymentRequestDTO request = new RedsysPaymentRequestDTO();
        request.setOrderId("order1");
        request.setAmount(java.math.BigDecimal.TEN);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> redsysPaymentService.processRedsysPayment(request));
    }

    @Test
    void testProcessRedsysPayment_mailThrows() {
        RedsysPaymentRequestDTO request = new RedsysPaymentRequestDTO();
        request.setOrderId("order2");
        request.setAmount(java.math.BigDecimal.ONE);
        Payment payment = Payment.builder().orderId("order2").amount(request.getAmount()).method(PaymentMethod.REDSYS).status(PaymentStatus.COMPLETED).build();
        payment.setId("pid2");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doThrow(new RuntimeException("Mail error")).when(orderMailPaymentService).sendOrderConfirmationEmail(anyString());
        Order order = new Order();
        order.setOrderId("order2");
        when(orderRepository.findByOrderId("order2")).thenReturn(java.util.Optional.of(order));
        assertThrows(RuntimeException.class, () -> redsysPaymentService.processRedsysPayment(request));
    }

    @Test
    void testProcessRedsysPayment_cartDeleteThrows() {
        RedsysPaymentRequestDTO request = new RedsysPaymentRequestDTO();
        request.setOrderId("order3");
        request.setAmount(java.math.BigDecimal.TEN);
        Payment payment = Payment.builder().orderId("order3").amount(request.getAmount()).method(PaymentMethod.REDSYS).status(PaymentStatus.COMPLETED).build();
        payment.setId("pid3");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(orderMailPaymentService).sendOrderConfirmationEmail(anyString());
        doThrow(new RuntimeException("Cart error")).when(cartRepository).deleteById(anyString());
        Order order = new Order();
        order.setOrderId("order3");
        when(orderRepository.findByOrderId("order3")).thenReturn(java.util.Optional.of(order));
        assertDoesNotThrow(() -> redsysPaymentService.processRedsysPayment(request));
    }
}
