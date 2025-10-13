# Librerías Recomendadas - Backend (Java/Spring Boot)

## 🔴 Críticas - Alta Prioridad

### 1. MapStruct - Mapeo Automático de Objetos
**Versión**: 1.5.5.Final  
**Estado actual**: Mappers manuales en paquete `mapper/` con mucho código boilerplate  
**Beneficio**: Reduce código de mappers en 80%, generación en tiempo de compilación

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

**Ejemplo**:
```java
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    List<OrderDTO> toDTOList(List<Order> orders);
}
```

---

### 2. Spring Boot Actuator + Micrometer
**Estado actual**: Sin visibilidad de métricas en producción  
**Beneficio**: Health checks, métricas (memoria, CPU, threads), integración con Prometheus/Grafana

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Configuración**:
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

**Endpoints**:
- `/actuator/health` - Estado de BD, MongoDB, etc.
- `/actuator/metrics` - Métricas del sistema
- `/actuator/prometheus` - Exportar a Prometheus

---

### 3. Resilience4j - Circuit Breaker y Resilience
**Versión**: 2.1.0  
**Estado actual**: Sin protección en llamadas externas (SendGrid, MongoDB, Kafka)  
**Beneficio**: Circuit breaker, retry con backoff, rate limiting

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
```

**Ejemplo**:
```java
@CircuitBreaker(name = "sendgrid", fallbackMethod = "sendEmailFallback")
@Retry(name = "sendgrid")
public void sendOrderConfirmation(String email, Order order) {
    mailSender.send(email, order);
}
```

**Configuración**:
```properties
resilience4j.circuitbreaker.instances.sendgrid.failure-rate-threshold=50
resilience4j.retry.instances.sendgrid.max-attempts=3
```

---

### 4. Flyway - Migraciones de Base de Datos
**Estado actual**: `spring.jpa.hibernate.ddl-auto=update` (peligroso en producción)  
**Beneficio**: Migraciones versionadas, reproducibles, rollback controlado

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**Configuración**:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

**Estructura**:
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_refresh_token_table.sql
└── V3__add_shipping_cost_to_orders.sql
```

---

## 🟡 Importantes - Media Prioridad

### 5. Spring Cache + Redis
**Beneficio**: Cachear catálogo de productos, reducir carga en BD

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

**Ejemplo**:
```java
@Cacheable(value = "products", key = "#id")
public Product getProductById(String id) {
    return productRepository.findById(id).orElseThrow();
}
```

---

### 6. Problem Spring Web - Errores RFC 7807
**Versión**: 0.29.1  
**Beneficio**: Respuestas de error estandarizadas

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>problem-spring-web</artifactId>
    <version>0.29.1</version>
</dependency>
```

**Respuesta generada**:
```json
{
  "type": "https://api.infinia.com/errors/product-not-found",
  "title": "Product Not Found",
  "status": 404,
  "detail": "Product with ID abc123 does not exist"
}
```

---

### 7. Bucket4j - Rate Limiting
**Versión**: 8.2.0  
**Beneficio**: Proteger API de abuso, limitar peticiones por IP

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.2.0</version>
</dependency>
```

---

### 8. ArchUnit - Tests de Arquitectura
**Versión**: 1.2.1  
**Beneficio**: Validar automáticamente reglas arquitectónicas

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo**:
```java
@ArchTest
static final ArchRule controllers_should_not_access_repositories = 
    noClasses()
        .that().resideInPackage("..controller..")
        .should().dependOnClassesThat().resideInPackage("..repository..");
```

---

## 🟢 Útiles - Baja Prioridad

### 9. Wiremock - Mocks para Tests
**Versión**: 3.3.1  
**Uso**: Simular SendGrid, sistemas de pago

```xml
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>3.3.1</version>
    <scope>test</scope>
</dependency>
```

---

### 10. Awaitility - Tests Asíncronos
**Versión**: 4.2.0  
**Uso**: Tests de Kafka

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>4.2.0</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo**:
```java
await().atMost(5, SECONDS).until(() -> orderRepository.findById(id).isPresent());
```

---

## 📊 Comparación de Impacto

| Librería | Esfuerzo | Impacto | Prioridad |
|----------|----------|---------|-----------|
| MapStruct | Medio | Alto | 🔴 |
| Actuator | Bajo | Alto | 🔴 |
| Resilience4j | Medio | Alto | 🔴 |
| Flyway | Alto | Alto | 🔴 |
| Redis Cache | Medio | Medio | 🟡 |
| Problem Web | Bajo | Medio | 🟡 |
| Bucket4j | Medio | Medio | 🟡 |
| ArchUnit | Bajo | Medio | 🟡 |
| Wiremock | Bajo | Bajo | 🟢 |
| Awaitility | Bajo | Bajo | 🟢 |
