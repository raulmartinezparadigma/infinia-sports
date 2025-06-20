package com.infinia.sports.security;

import com.infinia.sports.model.AdminUser;
import com.infinia.sports.repository.jpa.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[TRACE] Buscando admin: " + username);
        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("[TRACE] Admin no encontrado: " + username);
                    return new UsernameNotFoundException("Admin no encontrado: " + username);
                });
        System.out.println("[TRACE] Admin encontrado: " + admin.getUsername());
        System.out.println("[TRACE] Password recuperado: " + admin.getPassword());
        System.out.println("[TRACE] Habilitado: " + admin.isEnabled());
        return org.springframework.security.core.userdetails.User
                .withUsername(admin.getUsername())
                .password(admin.getPassword())
                .roles("ADMIN")
                .disabled(!admin.isEnabled())
                .build();
    }
}
