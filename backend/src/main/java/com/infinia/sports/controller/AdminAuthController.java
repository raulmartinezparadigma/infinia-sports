package com.infinia.sports.controller;

import com.infinia.sports.dto.AdminAuthRequestDTO;
import com.infinia.sports.security.AdminUserDetailsService;
import com.infinia.sports.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "admin-auth", description = "API de administración de autenticación")
public class AdminAuthController {

    private final AuthenticationManager adminAuthenticationManager;
    private final AdminUserDetailsService adminUserDetailsService;
    private final JwtService jwtService;

    @org.springframework.beans.factory.annotation.Autowired
    public AdminAuthController(
            @org.springframework.beans.factory.annotation.Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager,
            AdminUserDetailsService adminUserDetailsService,
            JwtService jwtService) {
        System.out.println("[TRACE] Constructor AdminAuthController: manager=" + adminAuthenticationManager.getClass().getName());
        this.adminAuthenticationManager = adminAuthenticationManager;
        this.adminUserDetailsService = adminUserDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminAuthRequestDTO request) {
        System.out.println("[TRACE] Entrando en login admin, manager=" + adminAuthenticationManager.getClass().getName());
        try {
            adminAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = adminUserDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
