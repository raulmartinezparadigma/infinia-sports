package com.infinia.sports.controller;

import com.infinia.sports.controller.CheckoutController;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private HttpServletRequest httpServletRequest;
/*
    @Test
    void testAddItemToCart() {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        
        CartItemDTO testCartItemDTO = CartItemDTO.builder()
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .build();
        
        Cart testCart = Cart.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>(List.of(Cart.CartItem.builder()
                        .id(itemId)
                        .productId("PROD-001")
                        .productName("Balón de fútbol profesional")
                        .quantity(2)
                        .unitPrice(new BigDecimal("49.99"))
                        .totalPrice(new BigDecimal("99.98"))
                        .attributes(new HashMap<>())
                        .build())))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("21.00"))
                .total(new BigDecimal("120.98"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(httpServletRequest.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
        when(httpServletRequest.getSession().getId()).thenReturn("test-session");
        when(checkoutService.addItemToCart(anyString(), anyString(), any(CartItemDTO.class)))
                .thenReturn(testCart);

        ResponseEntity<Cart> response = checkoutController.addItemToCart(testCartItemDTO, "test-user", httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(checkoutService).addItemToCart(anyString(), anyString(), any(CartItemDTO.class));
    }

    @Test
    void testGetCart() {
        String cartId = UUID.randomUUID().toString();
        
        Cart testCart = Cart.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>())
                .subtotal(new BigDecimal("0.00"))
                .tax(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(httpServletRequest.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
        when(httpServletRequest.getSession().getId()).thenReturn("test-session");
        when(checkoutService.getCart(anyString(), anyString()))
                .thenReturn(testCart);

        ResponseEntity<Cart> response = checkoutController.getCart("test-user", httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(checkoutService).getCart(anyString(), anyString());
    }

    @Test
    void testRemoveItemFromCart() {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        
        Cart testCart = Cart.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>(List.of(Cart.CartItem.builder()
                        .id(itemId)
                        .productId("PROD-001")
                        .productName("Balón de fútbol profesional")
                        .quantity(2)
                        .unitPrice(new BigDecimal("49.99"))
                        .totalPrice(new BigDecimal("99.98"))
                        .attributes(new HashMap<>())
                        .build())))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("21.00"))
                .total(new BigDecimal("120.98"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(httpServletRequest.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
        when(httpServletRequest.getSession().getId()).thenReturn("test-session");
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString()))
                .thenReturn(testCart);

        ResponseEntity<Cart> response = checkoutController.removeItemFromCart(itemId, "test-user", httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(checkoutService).removeItemFromCart(anyString(), anyString(), anyString());
    }

    @Test
    void testSaveAddresses() {
        String cartId = UUID.randomUUID().toString();
        
        Order.Address testAddress = Order.Address.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .city("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("+34600000000")
                .build();
        
        Cart testCart = Cart.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>())
                .subtotal(new BigDecimal("0.00"))
                .tax(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(httpServletRequest.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
        when(httpServletRequest.getSession().getId()).thenReturn("test-session");
        when(checkoutService.saveAddresses(anyString(), any(Order.Address.class), any(Order.Address.class), any(Boolean.class)))
                .thenReturn(testCart);

        ResponseEntity<Cart> response = checkoutController.saveAddresses(cartId, testAddress, testAddress, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(checkoutService).saveAddresses(anyString(), any(Order.Address.class), any(Order.Address.class), any(Boolean.class));
    }

    @Test
    void testConfirmOrder() {
        String cartId = UUID.randomUUID().toString();
        
        CheckoutDTO testCheckoutDTO = CheckoutDTO.builder()
                .cartId(cartId)
                .email("juan.perez@example.com")
                .shippingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .billingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .sameAsBillingAddress(true)
                .build();
        
        Order testOrder = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderId("ORD-" + System.currentTimeMillis())
                .language("es")
                .status("PENDIENTE")
                .email("juan.perez@example.com")
                .submitDate(LocalDateTime.now())
                .shippingGroups(new ArrayList<>())
                .shippingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .billingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .cart(Cart.builder()
                        .id(cartId)
                        .userId("test-user")
                        .sessionId("test-session")
                        .items(new ArrayList<>())
                        .subtotal(new BigDecimal("0.00"))
                        .tax(new BigDecimal("0.00"))
                        .discount(new BigDecimal("0.00"))
                        .total(new BigDecimal("0.00"))
                        .build())
                .build();
        
        when(checkoutService.confirmOrder(any(CheckoutDTO.class)))
                .thenReturn(testOrder);

        ResponseEntity<Order> response = checkoutController.confirmOrder(testCheckoutDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testOrder, response.getBody());
        verify(checkoutService).confirmOrder(any(CheckoutDTO.class));
    }

    @Test
    void testGetOrder() {
        String orderId = "ORD-" + System.currentTimeMillis();
        
        Order testOrder = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .language("es")
                .status("PENDIENTE")
                .email("juan.perez@example.com")
                .submitDate(LocalDateTime.now())
                .shippingGroups(new ArrayList<>())
                .shippingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .billingAddress(Order.Address.builder()
                        .firstName("Juan")
                        .lastName("Pérez")
                        .addressLine1("Calle Principal 123")
                        .city("Madrid")
                        .postalCode("28001")
                        .country("España")
                        .phoneNumber("+34600000000")
                        .build())
                .priceInfo(Order.PriceInfo.builder()
                        .subtotal(new BigDecimal("0.00"))
                        .tax(new BigDecimal("0.00"))
                        .discount(new BigDecimal("0.00"))
                        .total(new BigDecimal("0.00"))
                        .build())
                .build();
        
        when(checkoutService.getOrder(anyString()))
                .thenReturn(testOrder);

        ResponseEntity<Order> response = checkoutController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testOrder, response.getBody());
        verify(checkoutService).getOrder(anyString());
    }
    */


}
