package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.model.dto.OrderLineItemDTO;
import com.infinia.sports.model.dto.ShippingGroupDTO;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .email(order.getEmail())
                .language(order.getLanguage())
                .submitDate(order.getSubmitDate())
                .shippingGroups(toShippingGroupDTOList(order.getShippingGroups()))
                .shippingAddress(AddressMapper.toDTO(order.getShippingAddress()))
                .billingAddress(AddressMapper.toDTO(order.getBillingAddress()))
                .priceInfo(PriceInfoMapper.toDTO(order.getPriceInfo()))
                .taxInfo(TaxInfoMapper.toDTO(order.getTaxInfo()))
                .build();
    }

    private static List<ShippingGroupDTO> toShippingGroupDTOList(List<Order.ShippingGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(OrderMapper::toShippingGroupDTO).collect(Collectors.toList());
    }

    private static ShippingGroupDTO toShippingGroupDTO(Order.ShippingGroup group) {
        if (group == null) return null;
        return ShippingGroupDTO.builder()
                .id(group.getId())
                .shippingMethod(group.getShippingMethod())
                .shippingCost(group.getShippingCost())
                .lineItems(toLineItemDTOList(group.getLineItems()))
                .build();
    }

    private static List<OrderLineItemDTO> toLineItemDTOList(List<Order.LineItem> items) {
        if (items == null) return null;
        return items.stream().map(OrderMapper::toLineItemDTO).collect(Collectors.toList());
    }

    private static OrderLineItemDTO toLineItemDTO(Order.LineItem item) {
        if (item == null) return null;
        return OrderLineItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .attributes(item.getAttributes())
                .productImageUrl(item.getProductImageUrl())
                .build();
    }
}
