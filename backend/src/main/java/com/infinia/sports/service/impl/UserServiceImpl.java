package com.infinia.sports.service.impl;

import com.infinia.sports.exception.ResourceAlreadyExistsException;
import com.infinia.sports.model.Role;
import com.infinia.sports.model.User;
import com.infinia.sports.model.dto.RegisterRequestDTO;
import com.infinia.sports.repository.jpa.UserRepository;
import com.infinia.sports.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Implementación del servicio de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el nombre de usuario y email no existan previamente.
     * Asigna por defecto el rol USER.
     *
     * @param registerRequest DTO con los datos del usuario a registrar
     * @return Usuario registrado
     */
    @Override
    @Transactional
    public User registerUser(RegisterRequestDTO registerRequest) {
        // Verificar si el nombre de usuario ya existe
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("El nombre de usuario ya está en uso");
        }

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("El email ya está registrado");
        }

        // Crear y guardar el nuevo usuario
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .roles(Set.of(Role.USER))
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe un usuario con el email especificado.
     *
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
