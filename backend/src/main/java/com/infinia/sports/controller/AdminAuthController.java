package com.infinia.sports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinia.sports.dto.AdminAuthRequestDTO;
import com.infinia.sports.security.AdminUserDetailsService;
import com.infinia.sports.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "admin-auth", description = "API de administración de autenticación")
public class AdminAuthController {
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthController.class);

    private final AuthenticationManager adminAuthenticationManager;
    private final AdminUserDetailsService adminUserDetailsService;
    private final JwtService jwtService;

    @org.springframework.beans.factory.annotation.Autowired
    public AdminAuthController(
            @org.springframework.beans.factory.annotation.Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager,
            AdminUserDetailsService adminUserDetailsService,
            JwtService jwtService) {
        logger.info("[TRACE] Constructor AdminAuthController: manager={}", adminAuthenticationManager.getClass().getName());
        this.adminAuthenticationManager = adminAuthenticationManager;
        this.adminUserDetailsService = adminUserDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminAuthRequestDTO request) {
        logger.info("[TRACE] Entrando en login admin, manager={}", adminAuthenticationManager.getClass().getName());
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
