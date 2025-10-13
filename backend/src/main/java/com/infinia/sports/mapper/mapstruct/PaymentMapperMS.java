package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.dto.BizumPaymentResponseDTO;
import com.infinia.sports.model.dto.RedsysPaymentResponseDTO;
import com.infinia.sports.model.dto.TransferPaymentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para Payment
 */
@Mapper(config = MapStructConfig.class)
public interface PaymentMapperMS {
    
    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    BizumPaymentResponseDTO toBizumPaymentResponseDTO(Payment payment);
    
    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    RedsysPaymentResponseDTO toRedsysPaymentResponseDTO(Payment payment);
    
    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    @Mapping(target = "message", constant = "Pago por transferencia registrado. Pendiente de confirmación bancaria.")
    TransferPaymentResponseDTO toTransferPaymentResponseDTO(Payment payment);
}
