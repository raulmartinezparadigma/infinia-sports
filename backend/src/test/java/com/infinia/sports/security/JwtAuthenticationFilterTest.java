package com.infinia.sports.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup SecurityContextHolder with mock
        SecurityContextHolder.setContext(securityContext);
        
        // Create a generic test user
        userDetails = new User("user@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternal_PublicCartEndpoint() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/cart/items");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternal_PublicCheckoutEndpoint() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/checkout/summary");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternal_InvalidTokenFormat() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Token abc123");
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Given: A valid token for any user
        String jwt = "valid.user.token";
        String username = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/any-protected-resource");

        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);

        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then: Verify the filter uses the CustomUserDetailsService and sets authentication
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(jwtService).isTokenValid(jwt, userDetails);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Given
        String jwt = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);

        // Mocking the user details service is important
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(false);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(customUserDetailsService).loadUserByUsername("user@example.com");
        verify(jwtService).isTokenValid(jwt, userDetails);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_ExceptionInTokenProcessing() throws ServletException, IOException {
        // Given
        String jwt = "exception.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        when(jwtService.extractUsername(jwt)).thenThrow(new RuntimeException("Token parsing error"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_NullRolesInToken() throws ServletException, IOException {
        // Given
        String jwt = "null.roles.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);
        
        // Return null for roles claim
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(null);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(jwtService).isTokenValid(jwt, userDetails);
        // Authentication should still be set, but with original authorities from userDetails
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_EmptyRolesInToken() throws ServletException, IOException {
        // Given
        String jwt = "empty.roles.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders");
        
        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);
        
        // Return empty list for roles claim
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(Collections.emptyList());
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(jwtService).isTokenValid(jwt, userDetails);
        // Authentication should still be set, but with original authorities from userDetails
        verify(securityContext).setAuthentication(any());
    }
}
