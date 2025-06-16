package com.infinia.sports.config;

import org.springframework.context.annotation.Configuration;

/**
 * Nota: La configuración de repositorios JPA y MongoDB se ha movido a la clase principal InfiniaSportsApplication.
 * Esto evita la duplicación de configuraciones y los conflictos de beans.
 * 
 * @see com.infinia.sports.InfiniaSportsApplication
 */
@Configuration
public class RepositoryConfig {
    // La configuración de repositorios se realiza en la clase principal
}
