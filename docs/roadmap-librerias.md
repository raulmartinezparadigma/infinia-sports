# Roadmap de Implementación - Librerías

## 🎯 Resumen Ejecutivo

Este documento presenta un plan estructurado para implementar las librerías recomendadas en el proyecto Infinia Sports, priorizadas por impacto y esfuerzo.

---

## 📊 Top 5 Imprescindibles

### 1. MapStruct → Reduce código en 80%
**Problema**: 7 mappers manuales con código repetitivo  
**Solución**: Generación automática en compilación  
**Esfuerzo**: Medio | **Impacto**: Alto

### 2. Spring Boot Actuator → Visibilidad de producción
**Problema**: Sin métricas, health checks, ni monitorización  
**Solución**: Endpoints /actuator para DevOps  
**Esfuerzo**: Bajo | **Impacto**: Alto

### 3. Flyway → Migraciones seguras
**Problema**: ddl-auto=update peligroso en producción  
**Solución**: Migraciones SQL versionadas  
**Esfuerzo**: Alto | **Impacto**: Alto

### 4. Cucumber (BDD) → Responde a crítica del experto
**Problema**: Tests no alineados con negocio  
**Solución**: Features en Gherkin, legibles por todos  
**Esfuerzo**: Alto | **Impacto**: Muy Alto

### 5. Resilience4j → Robustez en producción
**Problema**: Sin protección en llamadas externas  
**Solución**: Circuit breaker, retry, rate limiting  
**Esfuerzo**: Medio | **Impacto**: Alto

---

## 📅 Plan de Implementación por Sprints

### Sprint 1 (2 semanas) - Fundamentos Backend
**Objetivo**: Mejorar mantenibilidad del código

#### Tareas:
1. **MapStruct**
   - Añadir dependencias
   - Refactorizar OrderMapper
   - Refactorizar ProductMapper
   - Refactorizar UserMapper

2. **Spring Boot Actuator**
   - Añadir dependencia
   - Configurar endpoints
   - Configurar Prometheus (opcional)
   - Documentar en README

**Entregables**:
- ✅ Código de mappers reducido en 80%
- ✅ Endpoints /actuator funcionando
- ✅ Dashboard de métricas (si Prometheus)

---

### Sprint 2 (2 semanas) - Testing y BDD
**Objetivo**: Responder a críticas del experto sobre arquitectura de tests

#### Tareas:
1. **Cucumber (BDD)**
   - Añadir dependencias
   - Crear estructura de features/
   - Crear steps para LoginTest
   - Crear steps para ShoppingCartTest
   - Crear steps para OrderHistoryTest

2. **Rest Assured**
   - Añadir dependencia
   - Migrar ProductControllerTest
   - Migrar OrderControllerTest
   - Migrar CartControllerTest

**Entregables**:
- ✅ 3 features en Gherkin funcionando
- ✅ Tests de API más legibles
- ✅ Documentación de BDD en README

---

### Sprint 3 (3 semanas) - Robustez y Producción
**Objetivo**: Preparar para producción

#### Tareas:
1. **Flyway**
   - Añadir dependencia
   - Crear V1__initial_schema.sql desde esquema actual
   - Crear V2__add_indexes.sql para optimización
   - Cambiar ddl-auto=validate
   - Probar migraciones en entorno de staging

2. **Resilience4j**
   - Añadir dependencia
   - Configurar Circuit Breaker para SendGrid
   - Configurar Retry para MongoDB
   - Añadir fallback methods
   - Tests de resilience

**Entregables**:
- ✅ BD gestionada por Flyway
- ✅ Protección contra fallos en servicios externos
- ✅ Tests de resilience

---

### Sprint 4 (1 semana) - Arquitectura y Calidad
**Objetivo**: Validar arquitectura automáticamente

#### Tareas:
1. **ArchUnit**
   - Añadir dependencia
   - Test: Controllers no acceden a Repositories
   - Test: Services no dependen de Controllers
   - Test: Entities en package model
   - Integrar en CI/CD

2. **Problem Spring Web**
   - Añadir dependencia
   - Crear excepciones personalizadas RFC 7807
   - Migrar @ExceptionHandler
   - Probar respuestas de error

**Entregables**:
- ✅ Tests arquitectónicos en CI/CD
- ✅ Respuestas de error estandarizadas

---

### Sprint 5 (Opcional) - Optimización
**Objetivo**: Mejorar rendimiento

#### Tareas:
1. **Redis Cache**
   - Configurar Redis local/Docker
   - Cachear catálogo de productos
   - Cachear detalles de producto
   - Métricas de cache hit/miss

2. **Bucket4j Rate Limiting**
   - Configurar límites por IP
   - Proteger endpoints públicos
   - Tests de rate limiting

**Entregables**:
- ✅ Mejora de tiempo de respuesta en 50%+
- ✅ Protección contra abuso de API

---

## 📈 Métricas de Éxito

### Antes vs Después

| Métrica | Antes | Después | Mejora |
|---------|-------|---------|--------|
| Líneas de código en mappers | ~500 | ~100 | -80% |
| Tiempo de respuesta /api/products | 200ms | <100ms | -50% |
| Cobertura de tests | 65% | 80%+ | +15% |
| Tests legibles por negocio | 0% | 100% | +100% |
| Visibilidad de producción | ❌ | ✅ | N/A |
| Migraciones controladas | ❌ | ✅ | N/A |

---

## 🚦 Semáforo de Prioridades

### 🔴 Implementar YA (Crítico)
- MapStruct
- Actuator
- Flyway
- Cucumber (BDD)
- Resilience4j

### 🟡 Implementar en 1-2 meses
- Redis Cache
- Problem Spring Web
- Bucket4j-
- ArchUnit
- Rest Assured

### 🟢 Implementar cuando haya tiempo
- Wiremock
- Awaitility
- Spring REST Docs
- Testcontainers (ampliado)

---

## 💰 Estimación de Esfuerzo Total

| Sprint | Semanas | Story Points | Riesgo |
|--------|---------|--------------|--------|
| Sprint 1 | 2 | 13 | Bajo |
| Sprint 2 | 2 | 21 | Medio |
| Sprint 3 | 3 | 34 | Alto |
| Sprint 4 | 1 | 8 | Bajo |
| Sprint 5 | 2 | 13 | Bajo |
| **TOTAL** | **10** | **89** | - |

---

## ⚠️ Riesgos y Mitigaciones

### Sprint 2: Cucumber (BDD)
**Riesgo**: Curva de aprendizaje alta para el equipo  
**Mitigación**: 
- Sesión de formación de 2h
- Pair programming en primeras features
- Documentación de patrones

### Sprint 3: Flyway
**Riesgo**: Migrar esquema existente puede romper datos  
**Mitigación**: 
- Backup completo de BD antes de migrar
- Probar en entorno de staging primero
- Rollback plan documentado

### Sprint 3: Resilience4j
**Riesgo**: Configuración incorrecta puede degradar rendimiento  
**Mitigación**: 
- Empezar con timeouts conservadores
- Monitorizar métricas con Actuator
- Ajustar thresholds basado en datos reales

---

## 📚 Recursos de Aprendizaje

### MapStruct
- Documentación oficial: https://mapstruct.org/
- Tutorial: Spring Boot + MapStruct

### Cucumber
- Documentación: https://cucumber.io/docs/cucumber/
- Libro: "The Cucumber Book" (recomendado)

### Flyway
- Documentación: https://flywaydb.org/documentation/
- Tutorial: Database migrations with Flyway

### Resilience4j
- Documentación: https://resilience4j.readme.io/
- Ejemplos: resilience4j-spring-boot2-demo

---

## ✅ Checklist de Implementación

### MapStruct
- [ ] Añadir dependencias al pom.xml
- [ ] Configurar annotation processor
- [ ] Crear interfaces @Mapper
- [ ] Refactorizar mappers existentes
- [ ] Tests unitarios de mappers
- [ ] Actualizar documentación

### Actuator
- [ ] Añadir dependencia
- [ ] Configurar endpoints en application.properties
- [ ] Probar /actuator/health
- [ ] Probar /actuator/metrics
- [ ] (Opcional) Configurar Prometheus
- [ ] Documentar en README

### Flyway
- [ ] Añadir dependencia
- [ ] Crear carpeta db/migration/
- [ ] Generar V1__initial_schema.sql
- [ ] Cambiar ddl-auto a validate
- [ ] Probar en H2
- [ ] Probar en PostgreSQL staging
- [ ] Backup de producción
- [ ] Aplicar en producción

### Cucumber
- [ ] Añadir dependencias
- [ ] Crear estructura de directorios
- [ ] Escribir primera feature (login)
- [ ] Implementar steps
- [ ] Configurar runner
- [ ] Migrar tests existentes
- [ ] Documentar convenciones

### Resilience4j
- [ ] Añadir dependencia
- [ ] Configurar circuit breaker
- [ ] Implementar fallback methods
- [ ] Configurar retry
- [ ] Tests de resilience
- [ ] Monitorizar métricas

---

## 🎓 Formación del Equipo

### Sesión 1: MapStruct (1h)
- Conceptos básicos
- Live coding: Refactorizar un mapper
- Q&A

### Sesión 2: BDD con Cucumber (2h)
- Sintaxis Gherkin
- Separación de concerns (Features/Steps/Pages)
- Live coding: Primera feature
- Ejercicio práctico

### Sesión 3: Flyway (1h)
- Conceptos de migraciones
- Naming conventions
- Demo: Crear y aplicar migración
- Rollback strategies

### Sesión 4: Resilience4j (1.5h)
- Patrones de resilience
- Circuit breaker en acción
- Configuración de timeouts
- Demo: Simular fallos

---

## 📞 Contacto y Soporte

Para dudas sobre la implementación:
1. Revisar documentación oficial de cada librería
2. Consultar ejemplos en este repositorio
3. Pair programming con miembro del equipo experimentado

**Documentación**:
- Backend: `/docs/librerias-backend.md`
- Testing: `/docs/librerias-testing.md`
- Este roadmap: `/docs/roadmap-librerias.md`
