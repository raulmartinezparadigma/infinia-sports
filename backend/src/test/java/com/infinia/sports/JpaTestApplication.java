package com.infinia.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test application class used specifically for JPA repository tests.
 * This class excludes MongoDB auto-configuration to avoid conflicts
 * when running JPA repository tests.
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.infinia.sports.repository.jpa")
public class JpaTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(JpaTestApplication.class, args);
    }
}
