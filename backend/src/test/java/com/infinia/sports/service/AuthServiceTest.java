package com.infinia.sports.service;

import com.infinia.sports.model.Role;
import com.infinia.sports.model.User;
import com.infinia.sports.model.Address;
import com.infinia.sports.model.dto.AuthRequestDTO;
import com.infinia.sports.model.dto.AuthResponseDTO;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.security.JwtService;
import com.infinia.sports.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de autenticación.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequestDTO registerRequest;
    private AuthRequestDTO authRequest;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(roles)
                .build();

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
    }

    @Test
    void testRegister() {
        // Configurar comportamiento de los mocks
        when(userService.registerUser(any(RegisterRequestDTO.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("test-jwt-token");

        // Ejecutar el método a probar
        AuthResponseDTO response = authService.register(registerRequest);

        // Verificar resultados
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(1, response.getRoles().size());
        assertEquals("USER", response.getRoles().get(0));

        // Verificar que se llamaron los métodos esperados
        verify(userService).registerUser(registerRequest);
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void testAuthenticate() {
        // Configurar comportamiento de los mocks
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("test-jwt-token");

        // Ejecutar el método a probar
        AuthResponseDTO response = authService.authenticate(authRequest);

        // Verificar resultados
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(1, response.getRoles().size());
        assertEquals("USER", response.getRoles().get(0));

        // Verificar que se llamaron los métodos esperados
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        verify(userService).findByUsername("testuser");
        verify(jwtService).generateToken(testUser);

        // --- Bloque adicional para probar el mapeo Address -> AddressDTO ---
        // Añadir una dirección al usuario de prueba antes de la autenticación
        Address address = new Address();
        address.setId(1L);
        address.setFirstName("Juan");
        address.setLastName("Pérez");
        address.setAddressLine1("Calle Falsa 123");
        address.setAddressLine2("Piso 4");
        address.setCity("Madrid");
        address.setState("Madrid");
        address.setPostalCode("28080");
        address.setCountry("España");
        address.setPhoneNumber("600123456");
        testUser.setAddresses(List.of(address));

        // Repetir la autenticación para obtener la respuesta con direcciones
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        AuthResponseDTO responseWithAddress = authService.authenticate(authRequest);

        assertNotNull(responseWithAddress.getAddresses());
        assertEquals(1, responseWithAddress.getAddresses().size());
        AddressDTO dto = responseWithAddress.getAddresses().get(0);
        assertEquals(address.getId(), dto.getId());
        assertEquals(address.getFirstName(), dto.getFirstName());
        assertEquals(address.getLastName(), dto.getLastName());
        assertEquals(address.getAddressLine1(), dto.getAddressLine1());
        assertEquals(address.getAddressLine2(), dto.getAddressLine2());
        assertEquals(address.getCity(), dto.getCity());
        assertEquals(address.getState(), dto.getState());
        assertEquals(address.getPostalCode(), dto.getPostalCode());
        assertEquals(address.getCountry(), dto.getCountry());
        assertEquals(address.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(testUser.getEmail(), dto.getEmail());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userService.findByUsername("nouser")).thenReturn(Optional.empty());
        AuthRequestDTO req = new AuthRequestDTO();
        req.setUsername("nouser");
        req.setPassword("pw");
        assertThrows(Exception.class, () -> authService.authenticate(req));
    }

    @Test
    void testAuthenticate_ThrowsAuthenticationException() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalid credentials"){});
        // Usa lenient para evitar UnnecessaryStubbingException
        lenient().when(userService.registerUser(any())).thenReturn(new User());

        // Act & Assert
        assertThrows(com.infinia.sports.exception.AuthenticationException.class, () -> {
            authService.authenticate(new AuthRequestDTO());
        });
    }

    @Test
    void testRegister_EmptyRoles() {
        User userNoRoles = User.builder().id(2L).username("nobody").roles(new HashSet<>()).build();
        when(userService.registerUser(any(RegisterRequestDTO.class))).thenReturn(userNoRoles);
        when(jwtService.generateToken(userNoRoles)).thenReturn("jwt");
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("nobody");
        req.setPassword("pw");
        req.setEmail("nobody@x.com");
        AuthResponseDTO res = authService.register(req);
        assertNotNull(res);
        assertEquals("nobody", res.getUsername());
        assertTrue(res.getRoles().isEmpty());
    }
}
