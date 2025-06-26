package com.infinia.sports.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.service.CheckoutService;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setup() {
        jakarta.servlet.http.HttpSession mockSession = mock(jakarta.servlet.http.HttpSession.class);
        lenient().when(httpServletRequest.getSession()).thenReturn(mockSession);
        lenient().when(httpServletRequest.getSession(true)).thenReturn(mockSession);
        lenient().when(mockSession.getId()).thenReturn("test-session-id");
    }

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

        CartDTO testCartDTO = CartDTO.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(List.of(testCartItemDTO))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("20.99"))
                .total(new BigDecimal("120.97"))
                .build();

        when(checkoutService.addItemToCart(anyString(), anyString(), any(CartItemDTO.class))).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = checkoutController.addItemToCart(testCartItemDTO, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).addItemToCart(anyString(), anyString(), any(CartItemDTO.class));
    }

    @Test
    void testUpdateCartItemQuantity() {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        CartItemDTO testCartItemDTO = CartItemDTO.builder()
                .id(itemId)
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .build();
        CartDTO testCartDTO = CartDTO.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(List.of(testCartItemDTO))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("20.99"))
                .total(new BigDecimal("120.97"))
                .build();
        when(checkoutService.updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt())).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = checkoutController.updateCartItemQuantity(itemId, testCartItemDTO, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void testRemoveItemFromCart() {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        CartItemDTO testCartItemDTO = CartItemDTO.builder()
                .id(itemId)
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .build();
        CartDTO testCartDTO = CartDTO.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(List.of(testCartItemDTO))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("20.99"))
                .total(new BigDecimal("120.97"))
                .build();
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString())).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = checkoutController.removeItemFromCart(itemId, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).removeItemFromCart(anyString(), anyString(), anyString());
    }

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

        CheckoutDTO testCheckoutDTO = CheckoutDTO.builder()
                .cartId(cartId)
                .email("juan.perez@example.com")
                .shippingAddress(testAddress)
                .billingAddress(testAddress)
                .sameAsBillingAddress(true)
                .build();

        Order testOrder = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderId("ORD-" + System.currentTimeMillis())
                .userId("test-user")
                .status("PENDIENTE")
                .build();

        when(checkoutService.confirmOrder(any(CheckoutDTO.class)))
                .thenReturn(testOrder);

        ResponseEntity<Order> response = checkoutController.confirmOrder(testCheckoutDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testOrder, response.getBody());
        verify(checkoutService).confirmOrder(any(CheckoutDTO.class));
    }
}
