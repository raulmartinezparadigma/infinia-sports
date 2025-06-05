package com.infinia.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Clase principal de la aplicación Infinia Sports
 * Punto de entrada para la aplicación Spring Boot
 * Configura el escaneo de repositorios JPA y MongoDB por subpaquete
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.infinia.sports.repository.jpa")
@EnableMongoRepositories(basePackages = "com.infinia.sports.repository.mongo")
public class InfiniaSportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfiniaSportsApplication.class, args);
    }
}

