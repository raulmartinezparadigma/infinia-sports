package com.infinia.sports.repository.mongo;

import com.infinia.sports.MongoTestApplication;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Abstract base class for MongoDB repository tests.
 * Uses in-memory MongoDB implementation for testing.
 */
@SpringBootTest(classes = MongoTestApplication.class)
public abstract class AbstractMongoDBTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @AfterEach
    void cleanUp() {
        // Drop all collections after each test to ensure a clean state
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.getCollection(collectionName).drop();
        }
    }
}
