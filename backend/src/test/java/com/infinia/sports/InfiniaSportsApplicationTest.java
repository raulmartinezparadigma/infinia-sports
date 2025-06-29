package com.infinia.sports;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Test unitario para la clase principal de la aplicación.
 * Verifica tanto la configuración de Spring como las anotaciones de repositorios.
 */
class InfiniaSportsApplicationTest {

    @Test
    void main_WhenCalled_StartsSpringApplication() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            // Configura el mock para que no haga nada cuando se llame a SpringApplication.run()
            mocked.when(() -> SpringApplication.run(eq(InfiniaSportsApplication.class), any(String[].class)))
                  .thenReturn(null);

            // Ejecuta el método main
            InfiniaSportsApplication.main(new String[]{});

            // Verifica que se llamó a SpringApplication.run() con los argumentos correctos
            mocked.verify(() -> SpringApplication.run(InfiniaSportsApplication.class, new String[]{}));
        }
    }

    @Test
    void class_HasSpringBootApplicationAnnotation() {
        // Verifica que la clase tenga la anotación @SpringBootApplication
        assertTrue(InfiniaSportsApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class
        ));
    }

    @Test
    void class_HasEnableJpaRepositoriesAnnotation() {
        // Verifica que la clase tenga la anotación @EnableJpaRepositories
        EnableJpaRepositories jpaAnnotation = InfiniaSportsApplication.class
            .getAnnotation(EnableJpaRepositories.class);
        
        assertNotNull(jpaAnnotation, "La clase debe tener @EnableJpaRepositories");
        assertEquals("com.infinia.sports.repository.jpa", jpaAnnotation.basePackages()[0],
            "El paquete base de JPA debe ser 'com.infinia.sports.repository.jpa'");
    }

    @Test
    void class_HasEnableMongoRepositoriesAnnotation() {
        // Verifica que la clase tenga la anotación @EnableMongoRepositories
        EnableMongoRepositories mongoAnnotation = InfiniaSportsApplication.class
            .getAnnotation(EnableMongoRepositories.class);
        
        assertNotNull(mongoAnnotation, "La clase debe tener @EnableMongoRepositories");
        assertEquals("com.infinia.sports.repository.mongo", mongoAnnotation.basePackages()[0],
            "El paquete base de MongoDB debe ser 'com.infinia.sports.repository.mongo'");
    }
}
