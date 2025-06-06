package com.infinia.sports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad principal de pago para MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String payerInfo;
    private String providerResponse;
}
