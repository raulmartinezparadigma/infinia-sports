package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.PriceInfoDTO;

public class PriceInfoMapper {
    public static PriceInfoDTO toDTO(Order.PriceInfo priceInfo) {
        if (priceInfo == null) return null;
        return PriceInfoDTO.builder()
                .subtotal(priceInfo.getSubtotal())
                .tax(priceInfo.getTax())
                .discount(priceInfo.getDiscount())
                .total(priceInfo.getTotal())
                .build();
    }
}
