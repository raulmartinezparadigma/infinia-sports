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
    void testDoFilterInternal_UserTokenForSharedEndpoint_UsesCustomUserDetailsService() throws ServletException, IOException {
        // Given: A token with only ROLE_USER accessing a shared endpoint like /api/orders
        String jwt = "valid.user.token";
        String username = "user@example.com";
        List<String> userRoles = Collections.singletonList("ROLE_USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(request.getRequestURI()).thenReturn("/api/orders"); // Use a shared endpoint that was previously problematic

        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Mock the extraction of roles from the token
        when(jwtService.extractClaim(eq(jwt), any(java.util.function.Function.class))).thenReturn(userRoles);

        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then: Verify the filter uses the CustomUserDetailsService because the token does not have ROLE_ADMIN
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(adminUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService).isTokenValid(jwt, userDetails);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_AdminToken_UsesAdminUserDetailsService() throws ServletException, IOException {
        // Given: A token with ROLE_ADMIN
        String jwt = "valid.admin.token";
        String username = "admin@example.com";
        List<String> adminRoles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        // The URI is irrelevant for the logic now, but we test a protected one for completeness
        when(request.getRequestURI()).thenReturn("/api/admin/some-resource");

        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Mock the extraction of roles from the token
        when(jwtService.extractClaim(eq(jwt), any(java.util.function.Function.class))).thenReturn(adminRoles);

        when(adminUserDetailsService.loadUserByUsername(username)).thenReturn(adminUserDetails);
        when(jwtService.isTokenValid(jwt, adminUserDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then: Verify the filter uses the AdminUserDetailsService because the token has ROLE_ADMIN
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(jwt);
        verify(adminUserDetailsService).loadUserByUsername(username);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService).isTokenValid(jwt, adminUserDetails);
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
