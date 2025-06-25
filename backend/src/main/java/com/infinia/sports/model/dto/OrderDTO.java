package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    private String orderId;
    private String userId;
    private String status;
    private String email;
    private String language;
    private LocalDateTime submitDate;
    private List<ShippingGroupDTO> shippingGroups;
    private AddressDTO shippingAddress;
    private AddressDTO billingAddress;
    private PriceInfoDTO priceInfo;
    private TaxInfoDTO taxInfo;
}
