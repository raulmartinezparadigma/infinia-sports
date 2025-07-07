package com.infinia.sports.repository.jpa;

import com.infinia.sports.model.AdminUser;
import com.infinia.sports.repository.jpa.AdminUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AdminUserRepository adminUserRepository;

    private AdminUser enabledAdmin;
    private AdminUser disabledAdmin;

    @BeforeEach
    void setUp() {
        // Clear previous test data
        adminUserRepository.deleteAll();
        
        // Create test admin users
        enabledAdmin = new AdminUser();
        enabledAdmin.setUsername("admin");
        enabledAdmin.setEmail("admin@example.com");
        enabledAdmin.setPassword("adminpass");
        enabledAdmin.setEnabled(true);
        
        disabledAdmin = new AdminUser();
        disabledAdmin.setUsername("disabledadmin");
        disabledAdmin.setEmail("disabled@example.com");
        disabledAdmin.setPassword("adminpass");
        disabledAdmin.setEnabled(false);
        
        // Save test users
        adminUserRepository.save(enabledAdmin);
        adminUserRepository.save(disabledAdmin);
    }
    
    @AfterEach
    void tearDown() {
        adminUserRepository.deleteAll();
    }

    @Test
    void testFindByUsername_ExistingEnabledAdmin() {
        // When
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("admin");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("admin@example.com", foundUser.get().getEmail());
        assertTrue(foundUser.get().isEnabled());
    }
    
    @Test
    void testFindByUsername_ExistingDisabledAdmin() {
        // When
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("disabledadmin");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("disabled@example.com", foundUser.get().getEmail());
        assertFalse(foundUser.get().isEnabled());
    }
    
    @Test
    void testFindByUsername_NonExistentAdmin() {
        // When
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("nonexistent");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testExistsByUsername_ExistingAdmin() {
        // When & Then
        assertTrue(adminUserRepository.existsByUsername("admin"));
    }
    
    @Test
    void testExistsByUsername_NonExistentAdmin() {
        // When & Then
        assertFalse(adminUserRepository.existsByUsername("nonexistent"));
    }
    
    @Test
    void testExistsByEmail_ExistingAdmin() {
        // When & Then
        assertTrue(adminUserRepository.existsByEmail("admin@example.com"));
    }
    
    @Test
    void testExistsByEmail_NonExistentAdmin() {
        // When & Then
        assertFalse(adminUserRepository.existsByEmail("nonexistent@example.com"));
    }
}
