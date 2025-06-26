package com.infinia.sports.service;

import com.infinia.sports.model.dto.OrderDTO;

public interface OrderService {
    OrderDTO getOrderById(String orderId);
}
