package com.infinia.sports.service;

import com.infinia.sports.model.dto.AuthRequestDTO;
import com.infinia.sports.model.dto.AuthResponseDTO;
import com.infinia.sports.model.dto.RegisterRequestDTO;

/**
 * Interfaz para el servicio de autenticación.
 */
public interface AuthService {
    
    /**
     * Autentica a un usuario y genera un token JWT.
     * 
     * @param authRequest DTO con las credenciales del usuario
     * @return DTO con el token JWT y la información del usuario
     */
    AuthResponseDTO authenticate(AuthRequestDTO authRequest);
    
    /**
     * Registra un nuevo usuario y genera un token JWT.
     * 
     * @param registerRequest DTO con los datos del usuario a registrar
     * @return DTO con el token JWT y la información del usuario
     */
    AuthResponseDTO register(RegisterRequestDTO registerRequest);
}
