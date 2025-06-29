package com.infinia.sports;

import com.infinia.sports.config.MongoTestConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.TestPropertySource;

/**
 * Configuración para pruebas de MongoDB que EXPLÍCITAMENTE no usa @SpringBootApplication
 * para evitar conflictos con otras configuraciones
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class, 
    HibernateJpaAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = "com.infinia.sports.repository.mongo")
@ComponentScan(basePackages = "com.infinia.sports.repository.mongo")
@Import(MongoTestConfig.class)
public class MongoTestApplication {
    // Sin método main
}
