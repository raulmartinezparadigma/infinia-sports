package com.infinia.sports.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {JwtProperties.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = {
    "app.jwt.secret=testSecretKeyForJwtPropertiesTest",
    "app.jwt.expiration=3600000"
})
class JwtPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void testJwtPropertiesBinding() {
        // Then
        assertNotNull(jwtProperties, "JwtProperties should not be null");
        assertEquals("testSecretKeyForJwtPropertiesTest", jwtProperties.getSecret(), 
                "Secret should match the test property value");
        assertEquals(3600000L, jwtProperties.getExpiration(), 
                "Expiration should match the test property value");
    }
    
    @Test
    void testSettersAndGetters() {
        // Given
        JwtProperties properties = new JwtProperties();
        
        // When
        properties.setSecret("newSecret");
        properties.setExpiration(7200000L);
        
        // Then
        assertEquals("newSecret", properties.getSecret());
        assertEquals(7200000L, properties.getExpiration());
    }
}
