package com.infinia.sports.controller;

import com.infinia.sports.model.dto.UserDTO;
import com.infinia.sports.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar la información de usuarios.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "usuarios", description = "API para gestionar usuarios")
public class UserController {

    private final UserService userService;

    /**
     * Obtiene la información del usuario autenticado actual.
     *
     * @return ResponseEntity con el DTO del usuario actual
     */
    @GetMapping("/me")
    @Operation(
        summary = "Obtener el usuario actual", 
        description = "Devuelve la información del usuario autenticado actual",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
