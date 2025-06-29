package com.infinia.sports.security;

import com.infinia.sports.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock JWT properties
        when(jwtProperties.getSecret()).thenReturn(SECRET_KEY);
        when(jwtProperties.getExpiration()).thenReturn(EXPIRATION_TIME);
        
        // Create test user with roles
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        userDetails = new User("testuser", "password", authorities);
    }

    @Test
    void testGenerateToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateToken(userDetails);
        
        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals("testuser", jwtService.extractUsername(token));
    }
    
    @Test
    void testGenerateTokenWithExtraClaims_ShouldIncludeClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("testKey", "testValue");
        
        // When
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        // Then
        assertNotNull(token);
        assertEquals("testValue", jwtService.extractClaim(token, claims -> claims.get("testKey")));
    }

    @Test
    void testIsTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    void testIsTokenValid_WithInvalidUsername_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("differentuser", "password", userDetails.getAuthorities());
        
        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    void testExtractAllRoles_ShouldReturnCorrectRoles() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        // When
        @SuppressWarnings("unchecked")
        List<String> roles = jwtService.extractClaim(token, claims -> (List<String>) claims.get("roles"));
        
        // Then
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }
    
    @Test
    void testExtractClaim_ShouldExtractSpecificClaim() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        
        // Then
        assertEquals("testuser", subject);
        assertNotNull(issuedAt);
    }
}
