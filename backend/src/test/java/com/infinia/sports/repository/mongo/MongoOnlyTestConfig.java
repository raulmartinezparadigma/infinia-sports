package com.infinia.sports.repository.mongo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuración específica para tests de MongoDB que solo activa lo necesario
 * y evita cualquier anotación @SpringBootConfiguration/@SpringBootApplication
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.infinia.sports.repository.mongo")
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class MongoOnlyTestConfig {
    // Clase de configuración mínima sin @SpringBootConfiguration
}
