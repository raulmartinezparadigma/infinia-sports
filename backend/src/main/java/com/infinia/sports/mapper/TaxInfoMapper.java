package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.TaxInfoDTO;

public class TaxInfoMapper {
    public static TaxInfoDTO toDTO(Order.TaxInfo taxInfo) {
        if (taxInfo == null) return null;
        return TaxInfoDTO.builder()
                .taxRate(taxInfo.getTaxRate())
                .taxRegion(taxInfo.getTaxRegion())
                .taxBreakdown(taxInfo.getTaxBreakdown())
                .build();
    }
}
