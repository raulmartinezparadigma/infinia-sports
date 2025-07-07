package com.infinia.sports.service;

import com.infinia.sports.exception.ResourceAlreadyExistsException;
import com.infinia.sports.exception.ResourceNotFoundException;
import com.infinia.sports.model.Address;
import com.infinia.sports.model.User;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.model.dto.UserDTO;
import com.infinia.sports.repository.jpa.AddressRepository;
import com.infinia.sports.repository.jpa.UserRepository;
import com.infinia.sports.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder, addressRepository);
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
        dto.setFirstName("Nombre");
        dto.setLastName("Apellido");
        dto.setAddressLine1("Calle 1");
        dto.setAddressLine2("");
        dto.setCity("Ciudad");
        dto.setState("Provincia");
        dto.setPostalCode("12345");
        dto.setCountry("España");
        dto.setPhoneNumber("600000000");
        dto.setNif("12345678A");
        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(userRepository.existsByEmail("nuevo@mail.com")).thenReturn(false);
        User user = new User();
        user.setUsername("nuevo");
        user.setEmail("nuevo@mail.com");
        when(passwordEncoder.encode("1234")).thenReturn("enc1234");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User result = userService.registerUser(dto);
        assertEquals("nuevo", result.getUsername());
        assertEquals("nuevo@mail.com", result.getEmail());
        assertNotNull(result.getAddresses());
        assertEquals(1, result.getAddresses().size());
        Address address = result.getAddresses().get(0);
        assertEquals("Nombre", address.getFirstName());
        assertEquals("Calle 1", address.getAddressLine1());
        assertEquals("España", address.getCountry());
        assertTrue(address.isMainAddress());
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

    @Test
    void getCurrentUser_success() {
        // Arrange
        String username = "currentUser";
        User user = User.builder().id(1L).username(username).email("current@test.com").build();
        
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // Act
            UserDTO result = userService.getCurrentUser();

            // Assert
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(user.getEmail(), result.getEmail());
        }
    }

    @Test
    void getCurrentUser_throwsException_whenNotAuthenticated() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
        }
    }

    @Test
    void getCurrentUser_throwsException_whenUserNotFoundInDb() {
        // Arrange
        String username = "ghostUser";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
            assertTrue(ex.getMessage().contains(username));
        }
    }
}
