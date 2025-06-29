package com.infinia.sports.security.user;

import com.infinia.sports.security.CustomUserDetailsService;
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
class UserAuthenticationManagerConfigTest {

    @InjectMocks
    private UserAuthenticationManagerConfig authenticationManagerConfig;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testUserAuthenticationManager() {
        // When
        AuthenticationManager authenticationManager = authenticationManagerConfig.userAuthenticationManager(
                userDetailsService, passwordEncoder);
        
        // Then
        assertNotNull(authenticationManager, "Authentication manager should not be null");
        assertTrue(authenticationManager instanceof ProviderManager, 
                "Authentication manager should be an instance of ProviderManager");
        
        ProviderManager providerManager = (ProviderManager) authenticationManager;
        assertEquals(1, providerManager.getProviders().size(), 
                "Provider manager should have one authentication provider");
    }
}
