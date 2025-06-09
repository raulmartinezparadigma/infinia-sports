package com.infinia.sports.service.impl;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.TransferPaymentRequestDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(TransferPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;

    public TransferPaymentResponseDTO processTransferPayment(TransferPaymentRequestDTO request) {
        // Crear entidad Payment con estado PENDING (la transferencia es offline)
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .method(PaymentMethod.TRANSFER)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        logger.info("[TransferService] Payment registrado para transferencia bancaria: {}", payment.getId());
        return TransferPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .message("Pago por transferencia registrado. Pendiente de confirmación bancaria.")
                .build();
    }
}
