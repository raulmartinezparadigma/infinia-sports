package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingGroupDTO {
    private String id;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private List<OrderLineItemDTO> lineItems;
}
