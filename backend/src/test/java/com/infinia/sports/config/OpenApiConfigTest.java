package com.infinia.sports.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void testMyOpenAPI() {
        // Given
        OpenApiConfig openApiConfig = new OpenApiConfig();
        
        // When
        OpenAPI openAPI = openApiConfig.myOpenAPI();
        
        // Then
        assertNotNull(openAPI);
        
        // Test Info object
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("API de Infinia Sports", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API para el e-commerce de productos deportivos", info.getDescription());
        
        // Test Contact object
        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals("Infinia Sports", contact.getName());
        assertEquals("info@infinia-sports.com", contact.getEmail());
        assertEquals("https://www.infinia-sports.com", contact.getUrl());
        
        // Test License object
        License license = info.getLicense();
        assertNotNull(license);
        assertEquals("Licencia de Infinia Sports", license.getName());
        assertEquals("https://www.infinia-sports.com/license", license.getUrl());
        
        // Test Server object
        assertFalse(openAPI.getServers().isEmpty());
        assertEquals(1, openAPI.getServers().size());
        Server server = openAPI.getServers().get(0);
        assertEquals("http://localhost:8080", server.getUrl());
        assertEquals("Servidor de desarrollo", server.getDescription());
    }
}
