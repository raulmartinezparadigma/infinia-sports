package com.infinia.sports.service;

import com.infinia.sports.model.User;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.model.dto.UserDTO;

import java.util.Optional;

/**
 * Interfaz para el servicio de usuarios.
 */
public interface UserService {
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param registerRequest DTO con los datos del usuario a registrar
     * @return Usuario registrado
     */
    User registerUser(RegisterRequestDTO registerRequest);
    
    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     * 
     * @param username Nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Obtiene la información del usuario autenticado actual.
     * 
     * @return DTO con la información del usuario actual
     */
    UserDTO getCurrentUser();
}
