# Librerías Recomendadas - Testing

## 🔴 Críticas - Alta Prioridad

### 1. Cucumber (BDD) - Tests en Lenguaje Natural
**Versión**: 7.14.0  
**Responde a**: Crítica del experto sobre ausencia de BDD en tests E2E  
**Beneficio**: Tests legibles por negocio, documentación ejecutable

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
```

**Estructura**:
```
src/test/resources/features/
├── login.feature
├── shopping_cart.feature
└── checkout.feature

src/test/java/com/infinia/sports/cucumber/
├── CucumberTestRunner.java
└── steps/
    ├── LoginSteps.java
    └── ShoppingCartSteps.java
```

**Ejemplo de feature**:
```gherkin
Feature: Gestión del Carrito de Compras
  Como usuario de la tienda
  Quiero poder añadir productos a mi carrito

  Scenario: Añadir un producto al carrito
    Given el usuario "testinfinia" está autenticado
    When hace clic en "Añadir al carrito" del primer producto
    Then el contador del carrito muestra "1"
    And el producto aparece en el carrito
```

**Implementación de steps**:
```java
@SpringBootTest
public class ShoppingCartSteps {
    
    @Autowired
    private CartService cartService;
    
    @Given("el usuario {string} está autenticado")
    public void userIsAuthenticated(String username) {
        this.userEmail = username + "@test.com";
    }
    
    @When("hace clic en {string} del primer producto")
    public void clicksAddToCart(String buttonText) {
        Product product = productService.getAllProducts().get(0);
        cartService.addItem(userEmail, product.getId(), 1);
    }
    
    @Then("el contador del carrito muestra {string}")
    public void cartCounterShows(String expectedCount) {
        CartDTO cart = cartService.getCart(userEmail);
        assertThat(cart.getItems().size()).isEqualTo(Integer.parseInt(expectedCount));
    }
}
```

---

### 2. Rest Assured - Tests de API
**Versión**: 5.4.0  
**Estado actual**: Tests con MockMvc (verboso)  
**Beneficio**: DSL fluido, más expresivo y legible

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo**:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    void setup() {
        RestAssured.port = port;
        token = authenticateAndGetToken("testinfinia", "123456");
    }
    
    @Test
    void should_get_all_products() {
        given()
            .auth().oauth2(token)
        .when()
            .get("/api/products")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].name", notNullValue())
            .body("[0].price", greaterThan(0f));
    }
    
    @Test
    void should_add_product_to_cart() {
        Map<String, Object> cartItem = Map.of(
            "productId", "product-123",
            "quantity", 2
        );
        
        given()
            .auth().oauth2(token)
            .contentType("application/json")
            .body(cartItem)
        .when()
            .post("/api/cart/items")
        .then()
            .statusCode(200)
            .body("items.size()", equalTo(1))
            .body("total", greaterThan(0f));
    }
}
```

---

## 🟡 Importantes - Media Prioridad

### 3. Testcontainers - Ampliación de Uso
**Versión**: 1.18.3 (ya incluida)  
**Estado actual**: Solo para MongoDB  
**Mejora**: Añadir PostgreSQL, Redis, Kafka

```xml
<!-- Ya tienes estas -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.18.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mongodb</artifactId>
    <version>1.18.3</version>
    <scope>test</scope>
</dependency>

<!-- Añadir estas -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.18.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.18.3</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo ampliado**:
```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");
    
    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:6");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    );
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

---

### 4. Mockito BDD - Sintaxis Given-When-Then
**Versión**: Incluido en Spring Boot Test  
**Beneficio**: Tests más legibles, alineados con BDD

```java
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    @Test
    void should_create_order_successfully() {
        // Given (preparar)
        Order order = new Order();
        order.setUserEmail("test@test.com");
        given(orderRepository.save(any())).willReturn(order);
        
        // When (ejecutar)
        Order result = orderService.createOrder(order);
        
        // Then (verificar)
        then(orderRepository).should().save(any(Order.class));
        assertThat(result).isNotNull();
    }
}
```

---

### 5. AssertJ - Assertions Fluidas
**Versión**: Incluido en Spring Boot Test  
**Beneficio**: Assertions más legibles y expresivas

```java
import static org.assertj.core.api.Assertions.*;

@Test
void should_calculate_order_total_correctly() {
    Order order = orderService.createOrder(request);
    
    // En lugar de:
    // assertEquals(expected, order.getTotal());
    
    // Usar AssertJ:
    assertThat(order)
        .isNotNull()
        .extracting(Order::getTotal, Order::getSubtotal, Order::getTax)
        .containsExactly(
            new BigDecimal("54.99"),
            new BigDecimal("50.00"),
            new BigDecimal("4.99")
        );
    
    assertThat(order.getItems())
        .hasSize(2)
        .extracting("productId")
        .containsExactlyInAnyOrder("prod-1", "prod-2");
}
```

---

### 6. Spring REST Docs - Documentación desde Tests
**Versión**: Incluido en Spring Boot  
**Beneficio**: Documentación siempre actualizada

```xml
<dependency>
    <groupId>org.springframework.restdocs</groupId>
    <artifactId>spring-restdocs-mockmvc</artifactId>
    <scope>test</scope>
</dependency>
```

**Ejemplo**:
```java
@AutoConfigureRestDocs
class ProductControllerDocTest {
    
    @Test
    void should_document_get_products() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andDo(document("get-products",
                responseFields(
                    fieldWithPath("[].id").description("Product ID"),
                    fieldWithPath("[].name").description("Product name"),
                    fieldWithPath("[].price").description("Product price")
                )
            ));
    }
}
```

---

## 🟢 Útiles - Baja Prioridad

### 7. JUnit 5 Parametrized Tests
**Ya incluido**: Spring Boot Test  
**Beneficio**: Reducir duplicación en tests

```java
@ParameterizedTest
@CsvSource({
    "1, 50.00, 50.00",
    "2, 50.00, 100.00",
    "5, 50.00, 250.00"
})
void should_calculate_subtotal(int quantity, BigDecimal price, BigDecimal expected) {
    CartItem item = new CartItem();
    item.setQuantity(quantity);
    item.setPrice(price);
    
    assertThat(item.getSubtotal()).isEqualByComparingTo(expected);
}

@ParameterizedTest
@ValueSource(strings = {"", "  ", "invalid-email"})
void should_reject_invalid_emails(String email) {
    assertThatThrownBy(() -> userService.register(email, "password"))
        .isInstanceOf(ValidationException.class);
}
```

---

### 8. Hamcrest - Matchers Adicionales
**Ya incluido**: Spring Boot Test  
**Uso**: Matchers más expresivos

```java
import static org.hamcrest.Matchers.*;

@Test
void should_filter_products_by_category() {
    List<Product> products = productService.findByCategory("SHOES");
    
    assertThat(products, hasSize(greaterThan(0)));
    assertThat(products, everyItem(hasProperty("category", equalTo("SHOES"))));
}
```

---

## 📋 Plan de Implementación Sugerido

### Sprint 1: Fundamentos
1. **Cucumber** - Implementar BDD para responder a crítica del experto
2. **Rest Assured** - Mejorar legibilidad de tests de API

### Sprint 2: Mejoras de Arquitectura
3. **ArchUnit** - Validar reglas arquitectónicas (Backend)
4. **Testcontainers** - Ampliar para PostgreSQL y Kafka

### Sprint 3: Documentación
5. **Spring REST Docs** - Generar documentación desde tests
6. **AssertJ** - Migrar assertions existentes

---

## 🎯 Beneficios de Implementar BDD con Cucumber

### Respuesta a Comentarios del Experto

**Comentario**: "Sin Page Objects, BDD ni parametrización, la suite no es robusta"

**Solución con Cucumber**:

1. **Separación de Concerns**:
   - Features (`.feature`) → Lenguaje de negocio
   - Steps (`*Steps.java`) → Lógica de test
   - Page Objects → Selectores e interacciones UI

2. **Estructura mejorada**:
```
playwright-tests/
├── src/test/resources/features/
│   ├── login.feature
│   ├── shopping_cart.feature
│   └── checkout.feature
├── src/test/java/
│   ├── steps/
│   │   ├── LoginSteps.java
│   │   └── ShoppingCartSteps.java
│   ├── pages/           # Page Object Model
│   │   ├── LoginPage.java
│   │   └── ProductListPage.java
│   └── config/
│       └── TestConfig.java  # Parametrización
```

3. **Parametrización**:
```properties
# test-config.properties
app.url=http://localhost:3000
api.url=http://localhost:8080
test.user.email=testinfinia@test.com
test.user.password=123456
```

**Resultado**: Tests mantenibles, escalables y alineados con negocio.

---

## 📊 Comparación de Esfuerzo vs Impacto

| Librería | Esfuerzo Implementación | Impacto en Tests | Prioridad |
|----------|-------------------------|------------------|-----------|
| Cucumber (BDD) | Alto | Muy Alto | 🔴 |
| Rest Assured | Bajo | Alto | 🔴 |
| Testcontainers (ampliado) | Medio | Alto | 🟡 |
| Mockito BDD | Bajo | Medio | 🟡 |
| AssertJ | Bajo | Medio | 🟡 |
| Spring REST Docs | Medio | Medio | 🟡 |
| JUnit Parametrized | Bajo | Bajo | 🟢 |
| Hamcrest | Bajo | Bajo | 🟢 |
