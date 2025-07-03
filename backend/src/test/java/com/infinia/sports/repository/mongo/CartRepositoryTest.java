package com.infinia.sports.repository.mongo;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Cart.CartItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoOnlyTestConfig.class)
class CartRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.4"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.autoconfigure.exclude", () -> "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");
    }

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String TEST_USER_ID = "test-user-123";
    private final String TEST_SESSION_ID = "test-session-123";

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }
    
    @AfterEach
    void cleanUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("Should save and retrieve a cart")
    void shouldSaveAndRetrieveCart() {
        // Given
        Cart cart = createTestCart();
        
        // When
        Cart savedCart = cartRepository.save(cart);
        Optional<Cart> retrievedCart = cartRepository.findById(savedCart.getId());
        
        // Then
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(retrievedCart.get().getItems().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find cart by userId")
    void shouldFindCartByUserId() {
        // Given
        Cart cart = createTestCart();
        cartRepository.save(cart);
        
        // When
        Optional<Cart> retrievedCart = cartRepository.findByUserId(TEST_USER_ID);
        
        // Then
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getUserId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should find cart by sessionId")
    void shouldFindCartBySessionId() {
        // Given
        Cart cart = createTestCart();
        cart.setUserId(null); // Set userId to null to simulate anonymous user
        cartRepository.save(cart);
        
        // When
        Optional<Cart> retrievedCart = cartRepository.findBySessionId(TEST_SESSION_ID);
        
        // Then
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getSessionId()).isEqualTo(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("Should delete cart by userId")
    void shouldDeleteCartByUserId() {
        // Given
        Cart cart = createTestCart();
        cartRepository.save(cart);
        
        // When
        cartRepository.deleteByUserId(TEST_USER_ID);
        
        // Then
        Optional<Cart> retrievedCart = cartRepository.findByUserId(TEST_USER_ID);
        assertThat(retrievedCart).isEmpty();
    }

    @Test
    @DisplayName("Should delete cart by userId or sessionId")
    void shouldDeleteCartByUserIdOrSessionId() {
        // Given
        Cart cartUser = createTestCart();
        Cart cartSession = createTestCart();
        cartSession.setUserId(null);
        cartSession.setSessionId("other-session-456");
        cartRepository.save(cartUser);
        cartRepository.save(cartSession);

        // Precondición: ambos existen
        assertThat(cartRepository.findByUserId(TEST_USER_ID)).isPresent();
        assertThat(cartRepository.findBySessionId("other-session-456")).isPresent();

        // When
        cartRepository.deleteByUserIdOrSessionId(TEST_USER_ID, "other-session-456");


        // Then
        assertThat(cartRepository.findByUserId(TEST_USER_ID)).isEmpty();
        assertThat(cartRepository.findBySessionId("other-session-456")).isEmpty();
    }

    private Cart createTestCart() {
        CartItem item = new CartItem();
        item.setId("item1");
        item.setProductId("prod1");
        item.setProductName("Test Product");
        item.setDescription("Product description");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("9.99"));
        item.setTotalPrice(new BigDecimal("9.99"));
        
        Map<String, String> attrs = new HashMap<>();
        attrs.put("color", "blue");
        attrs.put("size", "M");
        item.setAttributes(attrs);
        item.setProductImageUrl("http://example.com/image.jpg");

        LocalDateTime now = LocalDateTime.now();
        
        return Cart.builder()
                .userId(TEST_USER_ID)
                .sessionId(TEST_SESSION_ID)
                .createdAt(now)
                .updatedAt(now)
                .userEmail("test@example.com")
                .items(List.of(item))
                .subtotal(new BigDecimal("9.99"))
                .tax(new BigDecimal("0.99"))
                .total(new BigDecimal("10.98"))
                .build();
    }
}
