package com.infinia.sports.mapper;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class PaymentMapperTest {
    @Test
    void toBizumPaymentResponseDTO_mapsFieldsCorrectly() {
        Payment payment = Payment.builder()
                .id("1")
                .orderId("order-1")
                .method(null)
                .status(PaymentStatus.COMPLETED)
                .amount(BigDecimal.TEN)
                .currency("EUR")
                .transactionId("tx1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .payerInfo("payer")
                .providerResponse("ok")
                .build();
        BizumPaymentResponseDTO dto = PaymentMapper.toBizumPaymentResponseDTO(payment);
        assertEquals(payment.getId(), dto.getPaymentId());
        assertEquals(payment.getTransactionId(), dto.getTransactionId());
        assertEquals(payment.getStatus().name(), dto.getStatus());
        assertEquals(payment.getProviderResponse(), dto.getProviderResponse());
    }

    @Test
    void toRedsysPaymentResponseDTO_mapsFieldsCorrectly() {
        Payment payment = Payment.builder()
                .id("2")
                .orderId("order-2")
                .method(null)
                .status(PaymentStatus.FAILED)
                .amount(BigDecimal.ONE)
                .currency("EUR")
                .transactionId("tx2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .payerInfo("payer")
                .providerResponse("fail")
                .build();
        RedsysPaymentResponseDTO dto = PaymentMapper.toRedsysPaymentResponseDTO(payment);
        assertEquals(payment.getId(), dto.getPaymentId());
        assertEquals(payment.getTransactionId(), dto.getTransactionId());
        assertEquals(payment.getStatus().name(), dto.getStatus());
        assertEquals(payment.getProviderResponse(), dto.getProviderResponse());
    }

    @Test
    void toTransferPaymentResponseDTO_mapsFieldsCorrectly() {
        Payment payment = Payment.builder()
                .id("3")
                .orderId("order-3")
                .method(null)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.ZERO)
                .currency("EUR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .payerInfo("payer")
                .providerResponse(null)
                .build();
        TransferPaymentResponseDTO dto = PaymentMapper.toTransferPaymentResponseDTO(payment);
        assertEquals(payment.getId(), dto.getPaymentId());
        assertEquals(payment.getStatus().name(), dto.getStatus());
        assertEquals("Pago por transferencia registrado. Pendiente de confirmación bancaria.", dto.getMessage());
    }

    @Test
    void nullInput_returnsNull() {
        assertNull(PaymentMapper.toBizumPaymentResponseDTO(null));
        assertNull(PaymentMapper.toRedsysPaymentResponseDTO(null));
        assertNull(PaymentMapper.toTransferPaymentResponseDTO(null));
    }
}
