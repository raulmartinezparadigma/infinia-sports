package com.infinia.sports.config;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import jakarta.annotation.PreDestroy;

import java.net.InetSocketAddress;

/**
 * MongoDB test configuration providing an in-memory MongoDB server for testing.
 * This avoids the need for an external MongoDB process or embedded MongoDB.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.infinia.sports.repository.mongo")
public class MongoTestConfig {
    
    private MongoServer mongoServer;
    private MongoClient mongoClient;
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoDatabaseFactory factory = mongoDbFactory();
        return new MongoTemplate(factory);
    }
    
    @Bean
    public MongoDatabaseFactory mongoDbFactory() throws Exception {
        // Create and start an in-memory MongoDB server
        mongoServer = new MongoServer(new MemoryBackend());
        
        // Bind to a local port
        InetSocketAddress serverAddress = mongoServer.bind();
        String connectionString = "mongodb://" + serverAddress.getHostString() + ":" + serverAddress.getPort();
        
        // Create a MongoClient connected to the in-memory server
        mongoClient = MongoClients.create(connectionString);
        
        return new SimpleMongoClientDatabaseFactory(mongoClient, "test");
    }
    
    @PreDestroy
    public void cleanup() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        if (mongoServer != null) {
            mongoServer.shutdown();
        }
    }
}
