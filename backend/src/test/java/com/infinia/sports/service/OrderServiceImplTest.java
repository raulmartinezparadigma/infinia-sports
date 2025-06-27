package com.infinia.sports.service;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.impl.OrderServiceImpl;
import com.infinia.sports.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepository, productRepository);
    }

    @Test
    void testGetOrderById_Success() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }

    @Test
    void testGetOrderById_NotFound() {
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void testGetOrderById_Exception() {
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(orderId)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void testGetOrderById_NullShippingGroups() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setShippingGroups(null); // shippingGroups nulo
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }

    @Test
    void testGetOrderById_EmptyShippingGroups() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setShippingGroups(java.util.Collections.emptyList()); // shippingGroups vacío
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }

    @Test
    void testGetOrderById_ShippingGroupsWithInvalidProductId() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        Order.ShippingGroup sg = new Order.ShippingGroup();
        Order.LineItem li = new Order.LineItem();
        li.setProductId("not-a-uuid"); // productId inválido
        sg.setLineItems(java.util.List.of(li));
        order.setShippingGroups(java.util.List.of(sg));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }

    @Test
    void testGetOrderById_ShippingGroupsWithNullLineItems() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        Order.ShippingGroup sg = new Order.ShippingGroup();
        sg.setLineItems(null); // lineItems nulo
        order.setShippingGroups(java.util.List.of(sg));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }

    @Test
    void testGetOrderById_ShippingGroupsWithEmptyLineItems() {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderId(orderId);
        Order.ShippingGroup sg = new Order.ShippingGroup();
        sg.setLineItems(java.util.Collections.emptyList()); // lineItems vacío
        order.setShippingGroups(java.util.List.of(sg));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderDTO dto = orderService.getOrderById(orderId);
        assertNotNull(dto);
        assertEquals(orderId, dto.getOrderId());
    }
}
