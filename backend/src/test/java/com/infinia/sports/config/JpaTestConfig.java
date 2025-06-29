package com.infinia.sports.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test configuration for JPA repository tests that excludes MongoDB configuration
 * This allows repository tests to run without trying to connect to MongoDB
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.infinia.sports.repository.jpa"})
@EnableJpaRepositories(basePackages = "com.infinia.sports.repository.jpa")
@EntityScan(basePackages = "com.infinia.sports.model")
public class JpaTestConfig {
    // No additional beans needed, configuration is handled through annotations
}
