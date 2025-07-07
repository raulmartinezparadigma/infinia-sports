package com.infinia.sports.repository.jpa;

import com.infinia.sports.model.Role;
import com.infinia.sports.model.User;
import com.infinia.sports.repository.jpa.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User emailOnlyUser;

    @BeforeEach
    void setUp() {
        // Clear all existing data
        userRepository.deleteAll();
        
        // Create test users
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setEnabled(true);
        
        emailOnlyUser = new User();
        emailOnlyUser.setUsername("emailuser");
        emailOnlyUser.setPassword("password456");
        emailOnlyUser.setEmail("unique@example.com");
        emailOnlyUser.setRoles(Set.of(Role.USER));
        emailOnlyUser.setEnabled(true);
        
        // Save the users
        userRepository.save(testUser);
        userRepository.save(emailOnlyUser);
    }
    
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUsername_ExistingUser() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("Test", foundUser.get().getFirstName());
        assertEquals("User", foundUser.get().getLastName());
    }
    
    @Test
    void testFindByUsername_NonExistentUser() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testFindByEmail_ExistingUser() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
    
    @Test
    void testFindByEmail_NonExistentUser() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testExistsByUsername_ExistingUser() {
        // When & Then
        assertTrue(userRepository.existsByUsername("testuser"));
    }
    
    @Test
    void testExistsByUsername_NonExistentUser() {
        // When & Then
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }
    
    @Test
    void testExistsByEmail_ExistingUser() {
        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }
    
    @Test
    void testExistsByEmail_NonExistentUser() {
        // When & Then
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
