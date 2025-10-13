# Resumen Rápido - Librerías Recomendadas

## 📋 Índice Rápido

- [Backend](#backend)
- [Testing](#testing)
- [Enlaces a Documentación Completa](#enlaces)

---

## Backend

| # | Librería | Versión | Prioridad | Problema que Resuelve | Dependencia Maven |
|---|----------|---------|-----------|----------------------|-------------------|
| 1 | **MapStruct** | 1.5.5.Final | 🔴 | Mappers manuales con mucho boilerplate | `org.mapstruct:mapstruct` |
| 2 | **Actuator** | Spring BOM | 🔴 | Sin visibilidad de métricas en producción | `spring-boot-starter-actuator` |
| 3 | **Resilience4j** | 2.1.0 | 🔴 | Sin protección en llamadas externas | `resilience4j-spring-boot3` |
| 4 | **Flyway** | Spring BOM | 🔴 | ddl-auto=update peligroso | `flyway-core` |
| 5 | **Redis Cache** | Spring BOM | 🟡 | Consultas repetitivas a BD | `spring-boot-starter-data-redis` |
| 6 | **Problem Web** | 0.29.1 | 🟡 | Errores sin formato estándar | `problem-spring-web` |
| 7 | **Bucket4j** | 8.2.0 | 🟡 | Sin rate limiting | `bucket4j-core` |
| 8 | **ArchUnit** | 1.2.1 | 🟡 | Sin validación de arquitectura | `archunit-junit5` |
| 9 | **Wiremock** | 3.3.1 | 🟢 | Tests sin mocks HTTP | `wiremock-standalone` |
| 10 | **Awaitility** | 4.2.0 | 🟢 | Tests asíncronos difíciles | `awaitility` |

---

## Testing

| # | Librería | Versión | Prioridad | Problema que Resuelve | Dependencia Maven |
|---|----------|---------|-----------|----------------------|-------------------|
| 11 | **Cucumber** | 7.14.0 | 🔴 | Tests no legibles por negocio | `cucumber-java` |
| 12 | **Rest Assured** | 5.4.0 | 🔴 | Tests de API verbosos con MockMvc | `rest-assured` |
| 13 | **Testcontainers** | 1.18.3 | 🟡 | Ampliar uso: PostgreSQL, Kafka, Redis | `testcontainers:postgresql` |
| 14 | **Mockito BDD** | Spring BOM | 🟡 | Sintaxis no alineada con BDD | Incluido en `spring-boot-test` |
| 15 | **AssertJ** | Spring BOM | 🟡 | Assertions poco expresivas | Incluido en `spring-boot-test` |
| 16 | **REST Docs** | Spring BOM | 🟡 | Documentación desactualizada | `spring-restdocs-mockmvc` |

---

## Dependencias Maven - Copy/Paste Ready

### Backend Críticas

```xml
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Resilience4j -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### Testing Críticas

```xml
<!-- Cucumber -->
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

<!-- Rest Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

---

## Configuración Mínima

### Actuator
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### Flyway
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### Resilience4j
```properties
resilience4j.circuitbreaker.instances.sendgrid.failure-rate-threshold=50
resilience4j.retry.instances.sendgrid.max-attempts=3
```

---

## Impacto Esperado

### Reducción de Código
- **MapStruct**: -80% líneas en mappers
- **Cucumber**: +100% legibilidad para negocio
- **Rest Assured**: -40% líneas en tests de API

### Mejora de Rendimiento
- **Redis Cache**: -50% tiempo de respuesta en catálogo
- **Resilience4j**: +99.9% disponibilidad

### Mejora de Seguridad
- **Flyway**: Migraciones controladas, sin pérdida de datos
- **Bucket4j**: Protección contra abuso de API

### Mejora de Monitorización
- **Actuator**: Visibilidad 24/7 de salud de la aplicación
- **Prometheus**: Dashboard con métricas en tiempo real

---

## Quick Start - Implementación Rápida

### Día 1: Actuator (15 minutos)
```bash
# 1. Añadir dependencia al pom.xml
# 2. Añadir config en application.properties
# 3. Restart app
# 4. Abrir http://localhost:8080/actuator/health
```

### Día 2: MapStruct (2 horas)
```bash
# 1. Añadir dependencias
# 2. Crear @Mapper interface para OrderMapper
# 3. Eliminar código manual
# 4. Compilar: mvn clean compile
# 5. Verificar tests
```

### Semana 1: Cucumber (8 horas)
```bash
# 1. Añadir dependencias
# 2. Crear src/test/resources/features/login.feature
# 3. Crear LoginSteps.java
# 4. Ejecutar: mvn test
# 5. Ver reporte en target/cucumber-reports.html
```

---

## Comandos Útiles

### Verificar dependencias
```bash
mvn dependency:tree
mvn dependency:analyze
```

### Ejecutar tests
```bash
# Todos los tests
mvn clean verify

# Solo tests de Cucumber
mvn test -Dcucumber.filter.tags="@smoke"

# Solo tests de API con Rest Assured
mvn test -Dtest=*IntegrationTest
```

### Ver métricas de Actuator
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Flyway
```bash
# Ver estado de migraciones
mvn flyway:info

# Aplicar migraciones pendientes
mvn flyway:migrate

# Limpiar BD (solo en dev!)
mvn flyway:clean
```

---

## FAQ

### ¿Por qué MapStruct y no ModelMapper?
MapStruct genera código en **compilación** (más rápido), ModelMapper usa reflexión en **runtime** (más lento). MapStruct también detecta errores en compilación.

### ¿Actuator expone información sensible?
Configurado correctamente, no. Usa `show-details=when-authorized` en producción y protege endpoints con Spring Security.

### ¿Flyway puede romper datos existentes?
No si se usa correctamente. Flyway solo **añade** migraciones nuevas. Nunca modifica migraciones ya aplicadas.

### ¿Cucumber ralentiza los tests?
El overhead es mínimo (<5%). El beneficio de legibilidad y mantenibilidad compensa ampliamente.

### ¿Rest Assured reemplaza a MockMvc?
No necesariamente. Rest Assured es mejor para tests de integración (servidor real), MockMvc para tests unitarios de controllers.

---

## Métricas de Éxito

### KPIs a Medir

| Métrica | Herramienta | Target |
|---------|-------------|--------|
| Cobertura de tests | JaCoCo | >80% |
| Tiempo de build | Maven | <3 min |
| Features BDD escritas | Cucumber | 100% flujos críticos |
| Uptime | Actuator + Prometheus | >99.9% |
| Cache hit rate | Redis + Actuator | >70% |
| Líneas de código en mappers | SonarQube | -500 líneas |

---

## Enlaces

### Documentación Completa
- [Librerías Backend (Detallado)](./librerias-backend.md)
- [Librerías Testing (Detallado)](./librerias-testing.md)
- [Roadmap de Implementación](./roadmap-librerias.md)

### Documentación Externa
- [MapStruct Docs](https://mapstruct.org/)
- [Spring Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Resilience4j](https://resilience4j.readme.io/)
- [Flyway](https://flywaydb.org/documentation/)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [Rest Assured](https://rest-assured.io/)

---

## 🚀 Siguiente Paso

**Recomendación**: Empezar por **Sprint 1** del roadmap:
1. MapStruct (2 días)
2. Actuator (1 día)

Ver detalles completos en [roadmap-librerias.md](./roadmap-librerias.md)
