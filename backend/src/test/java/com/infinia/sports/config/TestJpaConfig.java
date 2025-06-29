package com.infinia.sports.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Test configuration for JPA repository tests that excludes MongoDB configuration
 * This allows repository tests to run without trying to connect to MongoDB
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@ComponentScan(
    basePackages = {"com.infinia.sports.model", "com.infinia.sports.repository.jpa"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = MongoRepository.class
    )
)
@EnableJpaRepositories(basePackages = "com.infinia.sports.repository.jpa")
@EntityScan(basePackages = "com.infinia.sports.model")
public class TestJpaConfig {
    // Configuration is handled through annotations
}
