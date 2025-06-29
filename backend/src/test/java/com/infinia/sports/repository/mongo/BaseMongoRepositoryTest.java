package com.infinia.sports.repository.mongo;

import com.infinia.sports.MongoTestApplication;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for all MongoDB repository tests.
 * This class provides configuration to isolate MongoDB tests from JPA dependencies.
 * All MongoDB repository tests should extend this class.
 */
@DataMongoTest
@ContextConfiguration(classes = MongoTestApplication.class)
@ActiveProfiles("mongo-test")
public abstract class BaseMongoRepositoryTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @AfterEach
    void cleanup() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        if (mongoTemplate != null) {
            for (String collectionName : mongoTemplate.getCollectionNames()) {
                mongoTemplate.getCollection(collectionName).drop();
            }
        }
    }

    protected abstract void setupTestData();
}
