package com.infinia.sports.security;

import com.infinia.sports.model.Role;
import com.infinia.sports.model.User;
import com.infinia.sports.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedpassword");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        testUser.setRoles(roles);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        
        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hashedpassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        // When/Then
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
        
        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_NullUsername() {
        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
        
        verify(userRepository).findByUsername(null);
    }
}
