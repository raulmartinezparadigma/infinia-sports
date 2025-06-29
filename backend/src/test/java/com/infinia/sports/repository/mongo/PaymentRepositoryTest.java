package com.infinia.sports.repository.mongo;

import com.infinia.sports.model.Payment;
import com.infinia.sports.model.PaymentMethod;
import com.infinia.sports.model.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoOnlyTestConfig.class)
class PaymentRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.4"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.autoconfigure.exclude", () -> "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");
    }

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TEST_ORDER_ID = "test-order-123";

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }
    
    @AfterEach
    void cleanUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("Should save and retrieve a payment")
    void shouldSaveAndRetrievePayment() {
        // Given
        Payment payment = createTestPayment();
        
        // When
        Payment savedPayment = paymentRepository.save(payment);
        Optional<Payment> retrievedPayment = paymentRepository.findById(savedPayment.getId());
        
        // Then
        assertThat(retrievedPayment).isPresent();
        assertThat(retrievedPayment.get().getOrderId()).isEqualTo(TEST_ORDER_ID);
        assertThat(retrievedPayment.get().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should find payment by orderId")
    void shouldFindPaymentByOrderId() {
        // Given
        Payment payment = createTestPayment();
        paymentRepository.save(payment);
        
        // When
        Optional<Payment> retrievedPayment = paymentRepository.findByOrderId(TEST_ORDER_ID);
        
        // Then
        assertThat(retrievedPayment).isPresent();
        assertThat(retrievedPayment.get().getOrderId()).isEqualTo(TEST_ORDER_ID);
    }

    @Test
    @DisplayName("Should return empty for non-existent orderId")
    void shouldReturnEmptyForNonExistentOrderId() {
        // When
        Optional<Payment> retrievedPayment = paymentRepository.findByOrderId("non-existent-order");
        
        // Then
        assertThat(retrievedPayment).isEmpty();
    }

    @Test
    @DisplayName("Should update payment status")
    void shouldUpdatePaymentStatus() {
        // Given
        Payment payment = createTestPayment();
        Payment savedPayment = paymentRepository.save(payment);
        
        // When
        savedPayment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(savedPayment);
        Optional<Payment> retrievedPayment = paymentRepository.findById(savedPayment.getId());
        
        // Then
        assertThat(retrievedPayment).isPresent();
        assertThat(retrievedPayment.get().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    private Payment createTestPayment() {
        LocalDateTime now = LocalDateTime.now();
        
        String payerInfo = "{\"name\": \"John Doe\", \"email\": \"john@example.com\"}";
        String providerResponse = "{\"transactionId\": \"txn_123456\", \"status\": \"success\"}";
        
        Payment payment = new Payment();
        payment.setOrderId(TEST_ORDER_ID);
        payment.setMethod(PaymentMethod.BIZUM);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(new BigDecimal("99.99"));
        payment.setCurrency("EUR");
        payment.setTransactionId("txn-123456");
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        payment.setPayerInfo(payerInfo);
        payment.setProviderResponse(providerResponse);
        
        return payment;
    }
}
