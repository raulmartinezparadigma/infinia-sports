package com.infinia.sports.service;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.TransferPaymentRequestDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.service.impl.TransferPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransferPaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderMailPaymentService orderMailPaymentService;
    @InjectMocks
    private TransferPaymentServiceImpl transferPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferPaymentService = new TransferPaymentServiceImpl(paymentRepository, orderMailPaymentService);
    }

    @Test
    void testProcessTransferPayment_sendsMailAndSavesPayment() {
        TransferPaymentRequestDTO request = new TransferPaymentRequestDTO();
        request.setOrderId("order123");
        request.setAmount(new java.math.BigDecimal("50.00"));
        Payment savedPayment = Payment.builder()
                .orderId("order123")
                .amount(request.getAmount())
                .method(PaymentMethod.TRANSFER)
                .status(PaymentStatus.PENDING)
                .build();
        savedPayment.setId("paymentId");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        TransferPaymentResponseDTO dto = transferPaymentService.processTransferPayment(request);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderMailPaymentService, times(1)).sendOrderConfirmationEmail("order123");
        assertNotNull(dto);
        assertEquals("order123", request.getOrderId());
        assertEquals("paymentId", dto.getPaymentId()); 
    }
}
