package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para peticiones de pago Redsys
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedsysPaymentRequestDTO {
    private String orderId;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;
    private BigDecimal amount;
}
