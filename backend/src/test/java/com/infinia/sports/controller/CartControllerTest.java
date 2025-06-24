package com.infinia.sports.controller;

import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.service.CheckoutService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLinkCartToUser_ReturnsCartDTO() {
        // Arrange
        String cartId = "cart123";
        String userId = "user1";
        String userEmail = "user1@example.com";
        CartDTO cartDTO = CartDTO.builder().id(cartId).userId(userId).userEmail(userEmail).build();

        // Mock SecurityContext
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
}
