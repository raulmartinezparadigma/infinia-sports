package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del pago Bizum
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizumPaymentResponseDTO {
    private String paymentId;
    private String transactionId;
    private String status; // PENDING, COMPLETED, FAILED
    private String providerResponse; // Respuesta mock de Bizum
}
