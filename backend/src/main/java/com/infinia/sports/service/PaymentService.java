package com.infinia.sports.service;

import com.infinia.sports.model.dto.PaymentInfoDTO;

public interface PaymentService {
    PaymentInfoDTO getPaymentInfoByOrderId(String orderId);
}
