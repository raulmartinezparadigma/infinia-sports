package com.infinia.sports.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferPaymentRequestDTO {
    private String orderId;
    private BigDecimal amount;
    private String titular; // Nombre del titular que realiza la transferencia
    // Otros campos si se requieren
}
