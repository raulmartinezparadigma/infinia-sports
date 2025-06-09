package com.infinia.sports.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferPaymentResponseDTO {
    private String paymentId;
    private String status;
    private String message;
}
