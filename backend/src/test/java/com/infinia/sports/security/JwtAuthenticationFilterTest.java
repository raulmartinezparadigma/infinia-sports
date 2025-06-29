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
import java.util.ArrayList;
import java.util.Arrays;
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
    private AdminUserDetailsService adminUserDetailsService;

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
    private UserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup SecurityContextHolder with mock
        SecurityContextHolder.setContext(securityContext);
        
        // Create test users
        userDetails = new User("user@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        adminUserDetails = new User("admin@example.com", "password", 
                Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
                ));
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/products");
        
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
        when(request.getRequestURI()).thenReturn("/api/products");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternal_ValidToken_UserEndpoint() throws ServletException, IOException {
        // Given
        String jwt = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/products");
        
        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);
        
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(roles);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(customUserDetailsService).loadUserByUsername("user@example.com");
        verify(adminUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService).isTokenValid(jwt, userDetails);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_ValidToken_AdminEndpoint() throws ServletException, IOException {
        // Given
        String jwt = "valid.admin.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/admin/users");
        
        when(jwtService.extractUsername(jwt)).thenReturn("admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(adminUserDetailsService.loadUserByUsername("admin@example.com")).thenReturn(adminUserDetails);
        when(jwtService.isTokenValid(jwt, adminUserDetails)).thenReturn(true);
        
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(roles);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(adminUserDetailsService).loadUserByUsername("admin@example.com");
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService).isTokenValid(jwt, adminUserDetails);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_ValidToken_OrdersEndpoint() throws ServletException, IOException {
        // Given
        String jwt = "valid.admin.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders/all");
        
        when(jwtService.extractUsername(jwt)).thenReturn("admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(adminUserDetailsService.loadUserByUsername("admin@example.com")).thenReturn(adminUserDetails);
        when(jwtService.isTokenValid(jwt, adminUserDetails)).thenReturn(true);
        
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(roles);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(adminUserDetailsService).loadUserByUsername("admin@example.com");
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService).isTokenValid(jwt, adminUserDetails);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Given
        String jwt = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/products");
        
        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(null);
        
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
        when(request.getRequestURI()).thenReturn("/api/products");
        
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
        when(request.getRequestURI()).thenReturn("/api/products");
        
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
        when(request.getRequestURI()).thenReturn("/api/products");
        
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
