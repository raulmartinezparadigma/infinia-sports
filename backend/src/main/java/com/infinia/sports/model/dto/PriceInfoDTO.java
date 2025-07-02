package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfoDTO {
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal tax;
}
