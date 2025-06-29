package com.infinia.sports.security;

import com.infinia.sports.model.AdminUser;
import com.infinia.sports.repository.jpa.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminUserDetailsServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminUserDetailsService adminUserDetailsService;

    private AdminUser testAdminUser;
    private AdminUser disabledAdminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create an enabled test admin user
        testAdminUser = new AdminUser();
        testAdminUser.setId(1L);
        testAdminUser.setUsername("admin");
        testAdminUser.setPassword("hashedpassword");
        testAdminUser.setEnabled(true);
        
        // Create a disabled test admin user
        disabledAdminUser = new AdminUser();
        disabledAdminUser.setId(2L);
        disabledAdminUser.setUsername("disabledadmin");
        disabledAdminUser.setPassword("hashedpassword");
        disabledAdminUser.setEnabled(false);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(testAdminUser));
        
        // When
        UserDetails userDetails = adminUserDetailsService.loadUserByUsername("admin");
        
        // Then
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("hashedpassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        
        // Check for ROLE_ADMIN authority
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        verify(adminUserRepository).findByUsername("admin");
    }
    
    @Test
    void testLoadUserByUsername_DisabledUser() {
        // Given
        when(adminUserRepository.findByUsername("disabledadmin")).thenReturn(Optional.of(disabledAdminUser));
        
        // When
        UserDetails userDetails = adminUserDetailsService.loadUserByUsername("disabledadmin");
        
        // Then
        assertNotNull(userDetails);
        assertEquals("disabledadmin", userDetails.getUsername());
        assertFalse(userDetails.isEnabled()); // Should be disabled
        
        verify(adminUserRepository).findByUsername("disabledadmin");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(adminUserRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        // When/Then
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            adminUserDetailsService.loadUserByUsername("nonexistent");
        });
        
        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(adminUserRepository).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_NullUsername() {
        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> {
            adminUserDetailsService.loadUserByUsername(null);
        });
        
        verify(adminUserRepository).findByUsername(null);
    }
}
