package com.infinia.sports.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        if (path.matches("^/(api/)?(cart|checkout)(/.*)?$")) {
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
            
            // Si hay un nombre de usuario y no hay autenticación en el contexto de seguridad
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Seleccionar el servicio adecuado según la ruta
                // Si la ruta es de administración, usar AdminUserDetailsService; si no, CustomUserDetailsService
                UserDetails userDetails;
                if (path.startsWith("/api/admin/")) {
                    // Ruta de administración: buscar en adminUserDetailsService
                    userDetails = this.adminUserDetailsService.loadUserByUsername(username);
                } else {
                    // Ruta normal: buscar en customUserDetailsService
                    userDetails = this.customUserDetailsService.loadUserByUsername(username);
                }
                
                // Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crear un token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Establecer los detalles de la autenticación
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Actualizar el contexto de seguridad con la autenticación
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // En caso de error en la validación del token, no establecer la autenticación
            logger.error("Error validando el token JWT", e);
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
