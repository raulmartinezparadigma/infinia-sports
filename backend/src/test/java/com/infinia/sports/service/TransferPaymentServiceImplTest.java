package com.infinia.sports.service;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.TransferPaymentRequestDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.service.impl.TransferPaymentServiceImpl;
import com.infinia.sports.mapper.mapstruct.PaymentMapperMS;
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
    @Mock
    private PaymentMapperMS paymentMapper;
    @InjectMocks
    private TransferPaymentServiceImpl transferPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferPaymentService = new TransferPaymentServiceImpl(paymentRepository, orderMailPaymentService, paymentMapper);
        
        // Configurar mock por defecto para paymentMapper
        when(paymentMapper.toTransferPaymentResponseDTO(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            return TransferPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .build();
        });
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

    @Test
    void testProcessTransferPayment_paymentSaveThrows() {
        TransferPaymentRequestDTO request = new TransferPaymentRequestDTO();
        request.setOrderId("order1");
        request.setAmount(java.math.BigDecimal.TEN);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> transferPaymentService.processTransferPayment(request));
    }

    @Test
    void testProcessTransferPayment_mailThrows() {
        TransferPaymentRequestDTO request = new TransferPaymentRequestDTO();
        request.setOrderId("order2");
        request.setAmount(java.math.BigDecimal.ONE);
        Payment payment = Payment.builder().orderId("order2").amount(request.getAmount()).method(PaymentMethod.TRANSFER).status(PaymentStatus.PENDING).build();
        payment.setId("pid2");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doThrow(new RuntimeException("Mail error")).when(orderMailPaymentService).sendOrderConfirmationEmail(anyString());
        assertThrows(RuntimeException.class, () -> transferPaymentService.processTransferPayment(request));
    }
}
