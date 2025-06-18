package com.infinia.sports.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de propiedades JWT.
 * Esta clase mapea las propiedades de JWT definidas en application.properties.
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    
    /**
     * Clave secreta para firmar los tokens JWT
     */
    private String secret;
    
    /**
     * Tiempo de expiración del token en milisegundos
     */
    private long expiration;
}
