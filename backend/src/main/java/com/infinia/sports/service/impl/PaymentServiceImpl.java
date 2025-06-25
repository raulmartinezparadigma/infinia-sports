package com.infinia.sports.service.impl;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.dto.PaymentInfoDTO;
import com.infinia.sports.repository.mongo.PaymentRepository;
import com.infinia.sports.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentInfoDTO getPaymentInfoByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado para el orderId: " + orderId));

        PaymentInfoDTO dto = new PaymentInfoDTO(payment.getMethod().name(), payment.getStatus().name());
        logger.info("[getPaymentInfoByOrderId] Pago encontrado y devuelto para orderId: {}. Método: {}, Estado: {}", orderId, dto.getMethod(), dto.getStatus());
        return dto;
    }
}
