package com.infinia.sports.repository.mongo;

import com.infinia.sports.model.Order;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoOnlyTestConfig.class)
class OrderRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.4"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.autoconfigure.exclude", () -> "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");
    }

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TEST_ORDER_ID = "order-123456";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_STATUS = "COMPLETED";
    private static final String TEST_USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }
    
    @AfterEach
    void cleanUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("Should save and retrieve an order")
    void shouldSaveAndRetrieveOrder() {
        // Given
        Order order = createMinimalTestOrder();
        
        // When
        Order savedOrder = orderRepository.save(order);
        Optional<Order> retrievedOrder = orderRepository.findById(savedOrder.getId());
        
        // Then
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getOrderId()).isEqualTo(TEST_ORDER_ID);
        assertThat(retrievedOrder.get().getUserId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should find order by orderId")
    void shouldFindOrderByOrderId() {
        // Given
        Order order = createMinimalTestOrder();
        orderRepository.save(order);
        
        // When
        Optional<Order> retrievedOrder = orderRepository.findByOrderId(TEST_ORDER_ID);
        
        // Then
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getOrderId()).isEqualTo(TEST_ORDER_ID);
    }

    @Test
    @DisplayName("Should find orders by email")
    void shouldFindOrdersByEmail() {
        // Given
        Order order1 = createMinimalTestOrder();
        Order order2 = createMinimalTestOrder();
        order2.setOrderId("order-789");
        orderRepository.saveAll(List.of(order1, order2));
        
        // When
        List<Order> orders = orderRepository.findByEmail(TEST_EMAIL);
        
        // Then
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should find orders by status")
    void shouldFindOrdersByStatus() {
        // Given
        Order order1 = createMinimalTestOrder();
        Order order2 = createMinimalTestOrder();
        order2.setOrderId("order-789");
        order2.setStatus("PENDING");
        orderRepository.saveAll(List.of(order1, order2));
        
        // When
        List<Order> completedOrders = orderRepository.findByStatus(TEST_STATUS);
        List<Order> pendingOrders = orderRepository.findByStatus("PENDING");
        
        // Then
        assertThat(completedOrders).hasSize(1);
        assertThat(completedOrders.get(0).getStatus()).isEqualTo(TEST_STATUS);
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Should find orders by date range")
    void shouldFindOrdersByDateRange() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        
        Order order1 = createMinimalTestOrder();
        Order order2 = createMinimalTestOrder();
        order2.setOrderId("order-789");
        order2.setSubmitDate(yesterday);
        orderRepository.saveAll(List.of(order1, order2));
        
        // When
        List<Order> recentOrders = orderRepository.findBySubmitDateBetween(yesterday.minusHours(1), tomorrow);
        List<Order> todayOrders = orderRepository.findBySubmitDateBetween(now.minusMinutes(5), tomorrow);
        
        // Then
        assertThat(recentOrders).hasSize(2);
        assertThat(todayOrders).hasSize(1);
    }

    private Order createMinimalTestOrder() {
        LocalDateTime now = LocalDateTime.now();
        
        Order order = new Order();
        order.setOrderId(TEST_ORDER_ID);
        order.setLanguage("es");
        order.setSubmitDate(now);
        order.setStatus(TEST_STATUS);
        order.setEmail(TEST_EMAIL);
        order.setUserId(TEST_USER_ID);
        
        return order;
    }
}
