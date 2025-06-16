package com.infinia.sports.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinia.sports.model.dto.AuthRequestDTO;
import com.infinia.sports.model.dto.AuthResponseDTO;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para el controlador de autenticación.
 */
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO registerRequest;
    private AuthRequestDTO authRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
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
    void testRegister() throws Exception {
        // Configurar comportamiento del mock
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        // Ejecutar la petición POST y verificar resultados
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void testAuthenticate() throws Exception {
        // Configurar comportamiento del mock
        when(authService.authenticate(any(AuthRequestDTO.class))).thenReturn(authResponse);

        // Ejecutar la petición POST y verificar resultados
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void testRegisterWithInvalidData() throws Exception {
        // Crear un DTO de registro con datos inválidos
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO();
        invalidRequest.setUsername(""); // Username vacío (inválido)
        invalidRequest.setPassword("pwd"); // Contraseña demasiado corta (inválida)
        invalidRequest.setEmail("invalid-email"); // Email inválido

        // Ejecutar la petición POST y verificar que se devuelve un error de validación
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateWithInvalidData() throws Exception {
        // Crear un DTO de autenticación con datos inválidos
        AuthRequestDTO invalidRequest = new AuthRequestDTO();
        invalidRequest.setUsername(""); // Username vacío (inválido)
        invalidRequest.setPassword(""); // Contraseña vacía (inválida)

        // Ejecutar la petición POST y verificar que se devuelve un error de validación
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
