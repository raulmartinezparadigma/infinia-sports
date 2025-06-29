package com.infinia.sports.repository.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import com.infinia.sports.JpaTestApplication;

/**
 * Base class for all JPA repository tests.
 * This class provides configuration to isolate JPA tests from MongoDB dependencies.
 * All JPA repository tests should extend this class.
 */
@DataJpaTest
@ContextConfiguration(classes = JpaTestApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@EntityScan("com.infinia.sports.model")
public abstract class BaseRepositoryTest {
    // Base test class configuration
}
