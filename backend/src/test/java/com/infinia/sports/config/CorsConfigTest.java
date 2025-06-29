package com.infinia.sports.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsConfigTest {

    /**
     * Test the CORS configuration by creating the configurer and examining it
     */
    @Test
    void testCorsConfigurer() {
        // Given
        CorsConfig corsConfig = new CorsConfig();
        
        // When
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        
        // Then
        assertNotNull(configurer, "WebMvcConfigurer should not be null");
        
        // We can't easily test the exact CORS configuration without complex mocking
        // Instead, ensure the configurer is a valid instance and doesn't throw exceptions
        CorsRegistry registry = new CorsRegistry();
        assertDoesNotThrow(() -> configurer.addCorsMappings(registry));
    }
    
    /**
     * Test the CORS configuration by checking the class contains the expected methods
     */
    @Test
    void testCorsConfigClassStructure() {
        // Given
        CorsConfig corsConfig = new CorsConfig();
        
        // When/Then - Verify corsConfigurer method exists and returns WebMvcConfigurer
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        assertNotNull(configurer);
        
        // Reflection can be used to check if addCorsMappings is overridden
        boolean hasAddCorsMappingsMethod = false;
        Class<?> clazz = configurer.getClass();
        try {
            clazz.getDeclaredMethod("addCorsMappings", CorsRegistry.class);
            hasAddCorsMappingsMethod = true;
        } catch (NoSuchMethodException e) {
            // Method not found
        }
        
        assertTrue(hasAddCorsMappingsMethod, "WebMvcConfigurer should override addCorsMappings");
    }
}
