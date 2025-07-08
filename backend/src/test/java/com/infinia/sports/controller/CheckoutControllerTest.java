package com.infinia.sports.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.CheckoutService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private CheckoutService checkoutService;

    @Test
    void testSaveAddresses() {
        String cartId = UUID.randomUUID().toString();

        AddressDTO testAddress = AddressDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .city("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("+34600000000")
                .build();

        CartDTO testCartDTO = CartDTO.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>())
                .subtotal(new BigDecimal("0.00"))
                .tax(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .build();

        when(checkoutService.saveAddresses(anyString(), any(AddressDTO.class), any(AddressDTO.class), any(Boolean.class)))
                .thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = checkoutController.saveAddresses(cartId, testAddress, testAddress, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).saveAddresses(anyString(), any(AddressDTO.class), any(AddressDTO.class), any(Boolean.class));
    }

    @Test
    void testConfirmOrder() {
        CheckoutDTO testCheckoutDTO = CheckoutDTO.builder()
                .cartId("test-cart-id")
                .build();

        OrderDTO testOrderDTO = OrderDTO.builder()
                .orderId("ORD-" + System.currentTimeMillis())
                .userId("test-user")
                .status("PENDING")
                .build();

        when(checkoutService.confirmOrder(any(CheckoutDTO.class)))
                .thenReturn(testOrderDTO);

        ResponseEntity<OrderDTO> response = checkoutController.confirmOrder(testCheckoutDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testOrderDTO, response.getBody());
        verify(checkoutService).confirmOrder(any(CheckoutDTO.class));
    }

    @Test
    void testConfirmOrder_Exception() {
        CheckoutDTO checkoutDTO = CheckoutDTO.builder().cartId("cartId").build();
        when(checkoutService.confirmOrder(any(CheckoutDTO.class))).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<OrderDTO> response = checkoutController.confirmOrder(checkoutDTO);
        assertEquals(400, response.getStatusCodeValue());
    }
}
