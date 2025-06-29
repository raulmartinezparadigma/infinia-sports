package com.infinia.sports.security.admin;

import com.infinia.sports.security.AdminAuthenticationManagerConfig;
import com.infinia.sports.security.AdminUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthenticationManagerConfigTest {

    @InjectMocks
    private AdminAuthenticationManagerConfig authenticationManagerConfig;

    @Mock
    private AdminUserDetailsService adminUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testAdminAuthenticationManager() {
        // When
        AuthenticationManager authenticationManager = authenticationManagerConfig.adminAuthenticationManager(
                adminUserDetailsService, passwordEncoder);
        
        // Then
        assertNotNull(authenticationManager, "Authentication manager should not be null");
        assertTrue(authenticationManager instanceof ProviderManager, 
                "Authentication manager should be an instance of ProviderManager");
        
        ProviderManager providerManager = (ProviderManager) authenticationManager;
        assertEquals(1, providerManager.getProviders().size(), 
                "Provider manager should have one authentication provider");
    }
}
