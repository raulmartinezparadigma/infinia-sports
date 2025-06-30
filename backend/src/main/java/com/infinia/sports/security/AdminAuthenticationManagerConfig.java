package com.infinia.sports.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminAuthenticationManagerConfig {
    @Bean("adminAuthenticationManager")
    public AuthenticationManager adminAuthenticationManager(AdminUserDetailsService adminUserDetailsService, PasswordEncoder passwordEncoder) {
        System.out.println("[TRACE] Creando bean adminAuthenticationManager con PasswordEncoder: " + passwordEncoder.getClass().getName());
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
