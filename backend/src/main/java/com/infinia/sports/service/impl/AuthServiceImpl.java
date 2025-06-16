package com.infinia.sports.service.impl;

import com.infinia.sports.exception.AuthenticationException;
import com.infinia.sports.model.Role;
import com.infinia.sports.model.User;
import com.infinia.sports.model.dto.AuthRequestDTO;
import com.infinia.sports.model.dto.AuthResponseDTO;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.security.JwtService;
import com.infinia.sports.service.AuthService;
import com.infinia.sports.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Autentica a un usuario y genera un token JWT.
     *
     * @param authRequest DTO con las credenciales del usuario
     * @return DTO con el token JWT y la información del usuario
     * @throws AuthenticationException Si las credenciales son inválidas
     */
    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        try {
            // Autenticar al usuario con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Buscar el usuario en la base de datos
            User user = userService.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

            // Generar el token JWT
            String token = jwtService.generateToken(user);

            // Convertir los roles a una lista de strings
            List<String> roles = user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toList());

            // Crear y devolver la respuesta
            return AuthResponseDTO.builder()
                    .token(token)
                    .username(user.getUsername())
                    .roles(roles)
                    .build();
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Credenciales inválidas");
        }
    }

    /**
     * Registra un nuevo usuario y genera un token JWT.
     *
     * @param registerRequest DTO con los datos del usuario a registrar
     * @return DTO con el token JWT y la información del usuario
     */
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        // Registrar el nuevo usuario
        User user = userService.registerUser(registerRequest);

        // Generar el token JWT
        String token = jwtService.generateToken(user);

        // Convertir los roles a una lista de strings
        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());

        // Crear y devolver la respuesta
        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }
}
