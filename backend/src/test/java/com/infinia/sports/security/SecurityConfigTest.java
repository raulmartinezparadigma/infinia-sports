package com.infinia.sports.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    @org.springframework.beans.factory.annotation.Qualifier("customUserDetailsService")
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @BeforeEach
    void setUp() {
        // No additional setup needed as mocks are initialized by MockitoExtension
    }

    @Test
    void testPasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        // Then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testAuthenticationProvider() {
        PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
        JwtAuthenticationFilter mockFilter = mock(JwtAuthenticationFilter.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        SecurityConfig config = new SecurityConfig(mockFilter, uds);
        SecurityConfig spy = spy(config);
        doReturn(mockEncoder).when(spy).passwordEncoder();

        AuthenticationProvider provider = spy.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);

        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;
        try {
            var field = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
            field.setAccessible(true);
            assertEquals(uds, field.get(daoProvider));
            verify(spy).passwordEncoder();
        } catch (Exception e) {
            fail("Could not verify UserDetailsService: " + e.getMessage());
        }
    }

    @Test
    void testAuthenticationManager() throws Exception {
        // Given
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);
        
        // When
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);
        
        // Then
        assertNotNull(result);
        assertEquals(mockAuthManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }
}
