package com.infinia.sports.controller;

import com.infinia.sports.model.dto.*;
import com.infinia.sports.service.PaymentService;
import com.infinia.sports.service.impl.BizumPaymentServiceImpl;
import com.infinia.sports.service.impl.RedsysPaymentServiceImpl;
import com.infinia.sports.service.impl.TransferPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {
    @Mock
    private BizumPaymentServiceImpl bizumPaymentService;
    @Mock
    private RedsysPaymentServiceImpl redsysPaymentService;
    @Mock
    private TransferPaymentServiceImpl transferPaymentService;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processBizumPayment_ReturnsResponse() {
        BizumPaymentRequestDTO request = mock(BizumPaymentRequestDTO.class);
        BizumPaymentResponseDTO responseDTO = mock(BizumPaymentResponseDTO.class);
        when(bizumPaymentService.processBizumPayment(request)).thenReturn(responseDTO);
        ResponseEntity<BizumPaymentResponseDTO> response = paymentController.processBizumPayment(request);
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void processRedsysPayment_ReturnsResponse() {
        RedsysPaymentRequestDTO request = mock(RedsysPaymentRequestDTO.class);
        RedsysPaymentResponseDTO responseDTO = mock(RedsysPaymentResponseDTO.class);
        when(redsysPaymentService.processRedsysPayment(request)).thenReturn(responseDTO);
        ResponseEntity<RedsysPaymentResponseDTO> response = paymentController.processRedsysPayment(request);
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void processTransferPayment_ReturnsResponse() {
        TransferPaymentRequestDTO request = mock(TransferPaymentRequestDTO.class);
        TransferPaymentResponseDTO responseDTO = mock(TransferPaymentResponseDTO.class);
        when(transferPaymentService.processTransferPayment(request)).thenReturn(responseDTO);
        ResponseEntity<TransferPaymentResponseDTO> response = paymentController.processTransferPayment(request);
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getPaymentInfoByOrderId_ReturnsPaymentInfo() {
        String orderId = "order123";
        PaymentInfoDTO dto = mock(PaymentInfoDTO.class);
        when(paymentService.getPaymentInfoByOrderId(orderId)).thenReturn(dto);
        ResponseEntity<PaymentInfoDTO> response = paymentController.getPaymentInfoByOrderId(orderId);
        assertEquals(dto, response.getBody());
    }
}
