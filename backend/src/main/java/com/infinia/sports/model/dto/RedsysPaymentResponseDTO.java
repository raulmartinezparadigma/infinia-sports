package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de pago Redsys
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedsysPaymentResponseDTO {
    private String paymentId;
    private String transactionId;
    private String status;
    private String providerResponse;
}
