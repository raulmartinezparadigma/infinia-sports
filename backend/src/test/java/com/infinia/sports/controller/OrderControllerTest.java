package com.infinia.sports.controller;

import com.infinia.sports.mapper.OrderMapper;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrder_ReturnsOrderDTO_WhenOrderExists() {
        String orderId = "ORD123";
        Order order = new Order();
        OrderDTO dto = new OrderDTO();
        when(orderService.getOrderById(orderId)).thenReturn(order);
        try (MockedStatic<OrderMapper> mockedMapper = Mockito.mockStatic(OrderMapper.class)) {
            mockedMapper.when(() -> OrderMapper.toDTO(order)).thenReturn(dto);
            ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dto, response.getBody());
        }
    }

    @Test
    void getOrder_ReturnsInternalServerError_OnException() {
        String orderId = "ORD123";
        when(orderService.getOrderById(orderId)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
