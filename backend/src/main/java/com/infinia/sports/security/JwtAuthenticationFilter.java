package com.infinia.sports.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro para la autenticación basada en JWT.
 * Intercepta todas las peticiones HTTP y valida el token JWT si está presente.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Obtener el header de autorización
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Permitir acceso anónimo a rutas públicas aunque no haya token
        String path = request.getRequestURI();
        // Permitir acceso anónimo a rutas públicas usando regex más robusto
        if (path.matches("^/(api/)?(cart|checkout|products)(/.*)?$")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Si no hay token o no empieza con "Bearer ", continuar con la cadena de filtros
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token JWT (eliminar "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            // Extraer el nombre de usuario del token
            username = jwtService.extractUsername(jwt);
            logger.info("JwtAuthenticationFilter - Usuario extraído del token: {}", username);
            
            // Si hay un nombre de usuario y no hay autenticación en el contexto de seguridad
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;

                // Decidir qué UserDetailsService usar basándose en los roles del token
                List<String> rolesInToken = jwtService.extractClaim(jwt, claims -> claims.get("roles", List.class));

                if (rolesInToken != null && rolesInToken.contains("ROLE_ADMIN")) {
                    // Rol de administrador detectado en el token
                    logger.info("JwtAuthenticationFilter - Rol ADMIN detectado. Usando AdminUserDetailsService para el usuario: {}", username);
                    userDetails = this.adminUserDetailsService.loadUserByUsername(username);
                } else {
                    // Rol de usuario normal o sin roles
                    logger.info("JwtAuthenticationFilter - Rol de usuario detectado. Usando CustomUserDetailsService para el usuario: {}", username);
                    userDetails = this.customUserDetailsService.loadUserByUsername(username);
                }
                
                // Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Extraer los roles del token JWT si existen
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                    
                    try {
                        logger.info("JwtAuthenticationFilter - Intentando extraer roles del token");
                        // Intentar extraer el claim 'roles' del token
                        List<String> roles = jwtService.extractClaim(jwt, claims -> {
                            Object rolesClaim = claims.get("roles");
                            logger.info("JwtAuthenticationFilter - Claim 'roles' del token: {}", rolesClaim);
                            
                            if (rolesClaim instanceof List) {
                                logger.info("JwtAuthenticationFilter - El claim 'roles' es una lista");
                                return (List<String>) rolesClaim;
                            }
                            logger.info("JwtAuthenticationFilter - El claim 'roles' NO es una lista o es null");
                            return null;
                        });
                        
                        // Si hay roles en el token, reconstruir las authorities
                        if (roles != null && !roles.isEmpty()) {
                            logger.info("JwtAuthenticationFilter - Roles encontrados en el token: {}", roles);
                            authorities = roles.stream()
                                    .map(role -> {
                                        logger.info("JwtAuthenticationFilter - Creando authority para rol: {}", role);
                                        return new SimpleGrantedAuthority(role);
                                    })
                                    .collect(Collectors.toList());
                        } else {
                            logger.info("JwtAuthenticationFilter - No se encontraron roles en el token o la lista está vacía");
                        }
                    } catch (Exception e) {
                        logger.info("JwtAuthenticationFilter - Error al extraer roles del token JWT: {}", e.getMessage());
                        e.printStackTrace();
                    }
                    
                    // Crear un token de autenticación con las authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );
                    
                    // Establecer los detalles de la autenticación
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Actualizar el contexto de seguridad con la autenticación
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Capturar específicamente la excepción de token expirado
            logger.warn("El token JWT ha expirado: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Devolver 401
            return; // Detener la cadena de filtros aquí
        } catch (Exception e) {
            // En caso de otros errores en la validación del token, no establecer la autenticación
            logger.error("Error validando el token JWT", e);
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /*
     * El método isAdminPath se elimina porque la decisión ahora se basa en los roles del token JWT,
     * lo cual es un enfoque más robusto y seguro, especialmente para endpoints compartidos.
     */

}
