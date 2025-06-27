package com.infinia.sports.service;

import com.infinia.sports.model.User;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.repository.jpa.UserRepository;
import com.infinia.sports.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsername_False() {
        when(userRepository.existsByUsername("nouser")).thenReturn(false);
        assertFalse(userService.existsByUsername("nouser"));
    }

    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("test@mail.com"));
    }

    @Test
    void testExistsByEmail_False() {
        when(userRepository.existsByEmail("no@mail.com")).thenReturn(false);
        assertFalse(userService.existsByEmail("no@mail.com"));
    }

    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertTrue(userService.findByUsername("testuser").isPresent());
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertTrue(userService.findByUsername("nouser").isEmpty());
    }

    @Test
    void testRegisterUser_UsernameExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("taken");
        dto.setEmail("new@mail.com");
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        Exception ex = assertThrows(com.infinia.sports.exception.ResourceAlreadyExistsException.class, () -> userService.registerUser(dto));
        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
    }

    @Test
    void testRegisterUser_EmailExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("newuser");
        dto.setEmail("taken@mail.com");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@mail.com")).thenReturn(true);
        Exception ex = assertThrows(com.infinia.sports.exception.ResourceAlreadyExistsException.class, () -> userService.registerUser(dto));
        assertEquals("El email ya está registrado", ex.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("nuevo");
        dto.setEmail("nuevo@mail.com");
        dto.setPassword("1234");
        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(userRepository.existsByEmail("nuevo@mail.com")).thenReturn(false);
        User user = new User();
        user.setUsername("nuevo");
        user.setEmail("nuevo@mail.com");
        when(passwordEncoder.encode("1234")).thenReturn("enc1234");
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.registerUser(dto);
        assertEquals("nuevo", result.getUsername());
        assertEquals("nuevo@mail.com", result.getEmail());
    }

    @Test
    void testRegisterUser_SaveThrows() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("nuevo");
        dto.setEmail("nuevo@mail.com");
        dto.setPassword("1234");
        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(userRepository.existsByEmail("nuevo@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("enc1234");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
    }
}
