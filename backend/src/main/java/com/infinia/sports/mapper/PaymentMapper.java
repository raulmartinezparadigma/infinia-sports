package com.infinia.sports.mapper;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;

/**
 * Utilidad para mapear la entidad Payment a los DTOs de respuesta de pago
 */
public class PaymentMapper {

    /**
     * Convierte un Payment a BizumPaymentResponseDTO
     */
    public static BizumPaymentResponseDTO toBizumPaymentResponseDTO(Payment payment) {
        if (payment == null) return null;
        // Mapeo solo de campos existentes en el DTO
        return BizumPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .providerResponse(payment.getProviderResponse())
                .build();
    }

    /**
     * Convierte un Payment a RedsysPaymentResponseDTO
     */
    public static RedsysPaymentResponseDTO toRedsysPaymentResponseDTO(Payment payment) {
        if (payment == null) return null;
        // Mapeo solo de campos existentes en el DTO
        return RedsysPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .providerResponse(payment.getProviderResponse())
                .build();
    }

    /**
     * Convierte un Payment a TransferPaymentResponseDTO
     */
    public static TransferPaymentResponseDTO toTransferPaymentResponseDTO(Payment payment) {
        if (payment == null) return null;
        // Mapeo solo de campos existentes en el DTO
        return TransferPaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .message("Pago por transferencia registrado. Pendiente de confirmación bancaria.")
                .build();
    }
}
