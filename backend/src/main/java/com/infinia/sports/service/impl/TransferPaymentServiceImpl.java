package com.infinia.sports.service.impl;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import com.infinia.sports.model.dto.TransferPaymentRequestDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.service.OrderMailPaymentService;
import com.infinia.sports.mapper.mapstruct.PaymentMapperMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(TransferPaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final OrderMailPaymentService orderMailPaymentService;
    private final PaymentMapperMS paymentMapper;

    public TransferPaymentServiceImpl(PaymentRepository paymentRepository, OrderMailPaymentService orderMailPaymentService,
                                      PaymentMapperMS paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderMailPaymentService = orderMailPaymentService;
        this.paymentMapper = paymentMapper;
    }

    public TransferPaymentResponseDTO processTransferPayment(TransferPaymentRequestDTO request) {
        // Crear entidad Payment con estado PENDING (la transferencia es offline)
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .method(PaymentMethod.TRANSFER)
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);
        logger.info("[TransferService] Payment registrado para transferencia bancaria: {}", saved.getId());
        // Enviar email de resumen de pedido tras registrar transferencia (centralizado)
        orderMailPaymentService.sendOrderConfirmationEmail(saved.getOrderId());
        TransferPaymentResponseDTO dto = paymentMapper.toTransferPaymentResponseDTO(saved);
        logger.info("[TransferService] DTO devuelto: {}", dto);
        return dto;
    }
}
