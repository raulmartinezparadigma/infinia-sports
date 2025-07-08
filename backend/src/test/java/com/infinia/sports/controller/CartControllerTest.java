package com.infinia.sports.controller;

import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext for linkCartToUser test
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock HttpServletRequest for other cart tests
        jakarta.servlet.http.HttpSession mockSession = mock(jakarta.servlet.http.HttpSession.class);
        lenient().when(httpServletRequest.getSession()).thenReturn(mockSession);
        lenient().when(httpServletRequest.getSession(true)).thenReturn(mockSession);
        lenient().when(mockSession.getId()).thenReturn("test-session-id");
    }

    @Test
    void testLinkCartToUser_ReturnsCartDTO() {
        // Arrange
        String cartId = "cart123";
        String userId = "user1";
        CartDTO cartDTO = CartDTO.builder().id(cartId).userId(userId).userEmail(userId).build();

        when(authentication.getName()).thenReturn(userId);
        when(checkoutService.linkCartToUser(cartId, userId, userId)).thenReturn(cartDTO);

        // Act
        ResponseEntity<CartDTO> response = cartController.linkCartToUser(cartId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(cartDTO, response.getBody());
        verify(checkoutService, times(1)).linkCartToUser(cartId, userId, userId);
    }

    @Test
    void testAddItemToCart() {
        CartItemDTO testCartItemDTO = CartItemDTO.builder().productId("PROD-001").quantity(2).build();
        CartDTO testCartDTO = CartDTO.builder().id(UUID.randomUUID().toString()).build();

        when(checkoutService.addItemToCart(anyString(), anyString(), any(CartItemDTO.class))).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = cartController.addItemToCart(testCartItemDTO, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).addItemToCart(anyString(), anyString(), any(CartItemDTO.class));
    }

    @Test
    void testAddItemToCart_Exception() {
        CartItemDTO cartItemDTO = CartItemDTO.builder().productId("PROD-001").build();
        when(checkoutService.addItemToCart(anyString(), anyString(), any(CartItemDTO.class))).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CartDTO> response = cartController.addItemToCart(cartItemDTO, "test-user", httpServletRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testGetCart_ReturnsCart_WhenServiceWorks() {
        CartDTO cartDTO = CartDTO.builder().sessionId("test-session-id").userId("test-user").build();
        when(checkoutService.getCart(anyString(), anyString())).thenReturn(cartDTO);
        ResponseEntity<CartDTO> response = cartController.getCart("test-user", httpServletRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void testGetCart_ReturnsEmptyCart_OnException() {
        when(checkoutService.getCart(anyString(), anyString())).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CartDTO> response = cartController.getCart("test-user", httpServletRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test-session-id", response.getBody().getSessionId());
        assertEquals("test-user", response.getBody().getUserId());
    }

    @Test
    void testUpdateCartItemQuantity() {
        String itemId = UUID.randomUUID().toString();
        CartItemDTO testCartItemDTO = CartItemDTO.builder().id(itemId).quantity(2).build();
        CartDTO testCartDTO = CartDTO.builder().id(UUID.randomUUID().toString()).build();
        when(checkoutService.updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt())).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = cartController.updateCartItemQuantity(itemId, testCartItemDTO, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void testUpdateCartItemQuantity_ResourceNotFound() {
        when(checkoutService.updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt())).thenThrow(new com.infinia.sports.exception.ResourceNotFoundException("Not found"));
        ResponseEntity<CartDTO> response = cartController.updateCartItemQuantity("itemId", CartItemDTO.builder().quantity(1).build(), "userId", httpServletRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateCartItemQuantity_OtherException() {
        when(checkoutService.updateCartItemQuantity(anyString(), anyString(), anyString(), anyInt())).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CartDTO> response = cartController.updateCartItemQuantity("itemId", CartItemDTO.builder().quantity(1).build(), "userId", httpServletRequest);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testRemoveItemFromCart() {
        String itemId = UUID.randomUUID().toString();
        CartDTO testCartDTO = CartDTO.builder().id(UUID.randomUUID().toString()).build();
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString())).thenReturn(testCartDTO);

        ResponseEntity<CartDTO> response = cartController.removeItemFromCart(itemId, "test-user", httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testCartDTO, response.getBody());
        verify(checkoutService).removeItemFromCart(anyString(), anyString(), anyString());
    }

    @Test
    void testRemoveItemFromCart_ResourceNotFound() {
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString())).thenThrow(new com.infinia.sports.exception.ResourceNotFoundException("Not found"));
        ResponseEntity<CartDTO> response = cartController.removeItemFromCart("itemId", "userId", httpServletRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testRemoveItemFromCart_OtherException() {
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CartDTO> response = cartController.removeItemFromCart("itemId", "userId", httpServletRequest);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testClearCart_Success() {
        ResponseEntity<Void> response = cartController.clearCart("userId", httpServletRequest);
        assertEquals(204, response.getStatusCodeValue());
        verify(checkoutService).clearCart(anyString(), anyString());
    }

    @Test
    void testClearCart_Exception() {
        doThrow(new RuntimeException("DB error")).when(checkoutService).clearCart(anyString(), anyString());
        ResponseEntity<Void> response = cartController.clearCart("userId", httpServletRequest);
        assertEquals(400, response.getStatusCodeValue());
    }
}
