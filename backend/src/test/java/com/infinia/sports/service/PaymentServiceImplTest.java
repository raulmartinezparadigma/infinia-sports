package com.infinia.sports.service;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.PaymentInfoDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository);
    }

    @Test
    void testGetPaymentInfoByOrderId_Success() {
        Payment payment = new Payment();
        payment.setMethod(PaymentMethod.TRANSFER);
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByOrderId("order1")).thenReturn(Optional.of(payment));
        PaymentInfoDTO dto = paymentService.getPaymentInfoByOrderId("order1");
        assertNotNull(dto);
        assertEquals("TRANSFER", dto.getMethod());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void testGetPaymentInfoByOrderId_NotFound() {
        when(paymentRepository.findByOrderId("order2")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> paymentService.getPaymentInfoByOrderId("order2"));
    }
}
