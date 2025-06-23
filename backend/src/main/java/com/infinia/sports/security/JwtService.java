package com.infinia.sports.security;

import com.infinia.sports.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Servicio para la generación y validación de tokens JWT.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae una reclamación específica del token JWT.
     *
     * @param token Token JWT
     * @param claimsResolver Función para extraer la reclamación
     * @return Reclamación extraída
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario.
     *
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        System.out.println("[TRACE] JwtService - Generando token para usuario: " + userDetails.getUsername());
        System.out.println("[TRACE] JwtService - Authorities del usuario: " + userDetails.getAuthorities());
        
        List<String> roles = userDetails.getAuthorities().stream()
            .map(authority -> {
                String role = authority.getAuthority();
                System.out.println("[TRACE] JwtService - Añadiendo rol al token: " + role);
                return role;
            })
            .collect(Collectors.toList());
            
        System.out.println("[TRACE] JwtService - Lista de roles para el claim: " + roles);
        claims.put("roles", roles);
        
        String token = generateToken(claims, userDetails);
        System.out.println("[TRACE] JwtService - Token generado con éxito");
        return token;
    }

    /**
     * Genera un token JWT con reclamaciones adicionales para un usuario.
     *
     * @param extraClaims Reclamaciones adicionales
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es válido para un usuario.
     *
     * @param token Token JWT
     * @param userDetails Detalles del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración de un token JWT.
     *
     * @param token Token JWT
     * @return Fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todas las reclamaciones de un token JWT.
     *
     * @param token Token JWT
     * @return Todas las reclamaciones
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma para los tokens JWT.
     *
     * @return Clave de firma
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
