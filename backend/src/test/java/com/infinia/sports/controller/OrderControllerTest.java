package com.infinia.sports.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.OrderService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

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
        OrderDTO dto = new OrderDTO();
        when(orderService.getOrderById(orderId)).thenReturn(dto);
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getOrder_ReturnsInternalServerError_OnException() {
        String orderId = "ORD123";
        when(orderService.getOrderById(orderId)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getOrder_ReturnsNotFound_OnResponseStatusException() {
        String orderId = "ORD123";
        when(orderService.getOrderById(orderId)).thenThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getOrdersByEmail_ReturnsOrderList_WhenOrdersExist() {
        String email = "test@example.com";
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.getOrdersByEmail(email)).thenReturn(List.of(orderDTO));

        ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getOrdersByEmail_ReturnsEmptyList_WhenNoOrdersExist() {
        String email = "test@example.com";
        when(orderService.getOrdersByEmail(email)).thenReturn(Collections.emptyList());

        ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void getOrdersByEmail_ReturnsBadRequest_WhenEmailIsInvalid() {
        ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByEmail(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ResponseEntity<List<OrderDTO>> responseEmpty = orderController.getOrdersByEmail("");
        assertEquals(HttpStatus.BAD_REQUEST, responseEmpty.getStatusCode());
    }

    @Test
    void getOrdersByEmail_ReturnsInternalServerError_OnException() {
        String email = "test@example.com";
        when(orderService.getOrdersByEmail(email)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByEmail(email);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
