package com.infinia.sports.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.infinia.sports.model.dto.AuthRequestDTO;
import com.infinia.sports.model.dto.AuthResponseDTO;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.service.AuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para el controlador de autenticación.
 */
public class AuthControllerTest {

    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    private RegisterRequestDTO registerRequest;
    private AuthRequestDTO authRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurar DTO de registro
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        // Configurar DTO de autenticación
        authRequest = new AuthRequestDTO();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        // Configurar respuesta de autenticación
        authResponse = AuthResponseDTO.builder()
                .token("test-jwt-token")
                .username("testuser")
                .roles(Arrays.asList("USER"))
                .build();
    }

    @Test
    void testRegister() {
        // Configurar comportamiento del mock
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        // Ejecutar el método register y verificar resultados
        ResponseEntity<AuthResponseDTO> response = authController.register(registerRequest);
        AuthResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("test-jwt-token", body.getToken());
        assertEquals("testuser", body.getUsername());
        assertEquals("USER", body.getRoles().get(0));
    }

    @Test
    void testAuthenticate() {
        // Configurar comportamiento del mock
        when(authService.authenticate(any(AuthRequestDTO.class))).thenReturn(authResponse);

        // Ejecutar el método authenticate y verificar resultados
        ResponseEntity<AuthResponseDTO> response = authController.authenticate(authRequest);
        AuthResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("test-jwt-token", body.getToken());
        assertEquals("testuser", body.getUsername());
        assertEquals("USER", body.getRoles().get(0));
    }



}
