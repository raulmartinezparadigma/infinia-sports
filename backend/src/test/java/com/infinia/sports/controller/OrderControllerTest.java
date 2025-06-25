package com.infinia.sports.controller;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.OrderService;
import com.infinia.sports.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
        String orderId = "123";
        Order order = mock(Order.class);
        OrderDTO dto = mock(OrderDTO.class);
        when(orderService.getOrderById(orderId)).thenReturn(order);
        mockStatic(OrderMapper.class).when(() -> OrderMapper.toDTO(order)).thenReturn(dto);

        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getOrder_ReturnsNotFound_WhenOrderNotFound() {
        String orderId = "123";
        when(orderService.getOrderById(orderId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getOrder_ReturnsInternalServerError_OnException() {
        String orderId = "123";
        when(orderService.getOrderById(orderId)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
