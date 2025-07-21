package com.infinia.sports.controller;

import com.infinia.sports.dto.AdminAuthRequestDTO;
import com.infinia.sports.security.CustomUserDetailsService;
import com.infinia.sports.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AdminAuthController adminAuthController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ReturnsToken_WhenCredentialsAreValid() {
        AdminAuthRequestDTO request = new AdminAuthRequestDTO();
        request.setUsername("admin");
        request.setPassword("password");
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        ResponseEntity<?> response = adminAuthController.login(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt-token", response.getBody());
    }

    @Test
    void login_ReturnsUnauthorized_WhenCredentialsInvalid() {
        AdminAuthRequestDTO request = new AdminAuthRequestDTO();
        request.setUsername("admin");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
        ResponseEntity<?> response = adminAuthController.login(request);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Credenciales inválidas", response.getBody());
    }

    @Test
    void login_ReturnsUnauthorized_OnOtherException() {
        AdminAuthRequestDTO request = new AdminAuthRequestDTO();
        request.setUsername("admin");
        request.setPassword("irrelevant");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Unexpected error"));
        ResponseEntity<?> response = adminAuthController.login(request);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Credenciales inválidas", response.getBody());
    }
}
