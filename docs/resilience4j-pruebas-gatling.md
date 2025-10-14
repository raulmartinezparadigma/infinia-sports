# Pruebas de Resilience4j con Gatling

**Autor:** Cascade AI  
**Fecha:** 14 de octubre de 2025  
**Propósito:** Guía para ejecutar y analizar pruebas de carga que demuestran las estrategias de Resilience4j

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Configuración Previa](#configuración-previa)
3. [Ejecución de Pruebas](#ejecución-de-pruebas)
4. [Qué Buscar en los Resultados](#qué-buscar-en-los-resultados)
5. [Monitoreo con Actuator](#monitoreo-con-actuator)
6. [Interpretación de Métricas](#interpretación-de-métricas)
7. [Escenarios de Fallo Simulados](#escenarios-de-fallo-simulados)

---

## 🎯 Introducción

Esta guía te ayudará a:
- **Ejecutar pruebas de carga** específicas para estresar los mecanismos de Resilience4j
- **Observar en tiempo real** cómo funcionan Circuit Breaker, Retry y Fallbacks
- **Analizar métricas** para validar la resiliencia de la aplicación

### Servicios Protegidos

| Servicio | Endpoint | Estrategias Aplicadas |
|----------|----------|----------------------|
| **CheckoutService.getCart()** | `GET /api/cart` | `@CircuitBreaker`, `@Retry`, `fallbackGetCart()` |
| **OrderMailPaymentService** | (interno) | `@CircuitBreaker`, `@Retry`, `fallbackSendEmail()` |

---

## 🔧 Configuración Previa

### 1. Requisitos

```bash
✅ Backend ejecutándose en http://localhost:8080
✅ MongoDB ejecutándose en localhost:27017
✅ Base de datos H2 inicializada (Flyway)
✅ Usuario de prueba: drrivera / Daniel2008
```

### 2. Verificar Configuración de Resilience4j

Revisa `backend/src/main/resources/application.properties`:

```properties
# Circuit Breaker - MongoDB
resilience4j.circuitbreaker.instances.mongoService.slidingWindowSize=100
resilience4j.circuitbreaker.instances.mongoService.minimumNumberOfCalls=10
resilience4j.circuitbreaker.instances.mongoService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.mongoService.waitDurationInOpenState=60000

# Retry - MongoDB
resilience4j.retry.instances.mongoService.maxAttempts=3
resilience4j.retry.instances.mongoService.waitDuration=1000
resilience4j.retry.instances.mongoService.exponentialBackoffMultiplier=2
```

**Interpretación:**
- El Circuit Breaker se **abrirá** cuando haya un 50% de fallos en las últimas 100 llamadas
- Cuando se abra, permanecerá **OPEN durante 60 segundos**
- Los reintentos se harán hasta **3 veces** con espera exponencial (1s, 2s, 4s)

### 3. Activar Endpoints de Monitoreo

Verifica que Actuator exponga las métricas de Resilience4j:

```properties
management.endpoints.web.exposure.include=health,metrics,circuitbreakers,circuitbreakerevents,retries,retryevents
management.health.circuitbreakers.enabled=true
```

---

## 🚀 Ejecución de Pruebas

### Opción 1: Ejecutar Solo la Simulación de Resilience4j

Desde el directorio raíz del proyecto:

```bash
cd gatling-tests
mvn gatling:test -Dgatling.simulationClass=com.infinia.sports.performance.Resilience4jStressSimulation
```

### Opción 2: Ejecutar Todas las Simulaciones

```bash
cd gatling-tests
mvn gatling:test
```

### Opción 3: Ejecutar con Perfiles de Carga Personalizados

Puedes ajustar la carga modificando el archivo `Resilience4jStressSimulation.scala`:

```scala
// Carga LIGERA (desarrollo/debug)
cartStressScenario.inject(
  rampUsers(5).during(10.seconds)
)

// Carga MEDIA (staging/QA)
cartStressScenario.inject(
  rampUsers(20).during(10.seconds),
  constantUsersPerSec(10).during(20.seconds)
)

// Carga EXTREMA (prueba de ruptura)
cartStressScenario.inject(
  rampUsers(50).during(10.seconds),
  constantUsersPerSec(50).during(60.seconds)
)
```

---

## 🔍 Qué Buscar en los Resultados

### 1. Durante la Ejecución

Observa los logs del backend en tiempo real:

```bash
# En otra terminal, sigue los logs del backend
tail -f backend/logs/application.log

# O si usas Spring Boot directamente:
# Los logs aparecerán en la consola donde ejecutaste `mvn spring-boot:run`
```

**Logs Esperados - Funcionamiento Normal:**

```log
[CartController] GET /api/cart - sessionId=abc123, userId=user456
[CheckoutServiceImpl] Carrito encontrado: id=cart789, items=3
```

**Logs Esperados - Retry en Acción:**

```log
[Retry-mongoService] Retry attempt 1/3 for method getCart
[Retry-mongoService] Retry attempt 2/3 for method getCart
[CheckoutServiceImpl] Carrito recuperado tras reintento
```

**Logs Esperados - Circuit Breaker OPEN:**

```log
⚠️ FALLBACK: MongoDB no disponible al obtener carrito. sessionId=abc123, userId=user456. Causa: CallNotPermittedException
[CircuitBreaker-mongoService] Circuit Breaker is OPEN, calling fallback
[CheckoutServiceImpl] Devolviendo carrito vacío (fallback)
```

### 2. Reporte HTML de Gatling

Al finalizar, Gatling genera un reporte en:

```
gatling-tests/target/gatling/resilience4jstresssimulation-[timestamp]/index.html
```

Abre este archivo en tu navegador. Busca:

#### ✅ Métricas de Éxito

| Métrica | Valor Esperado | Significado |
|---------|----------------|-------------|
| **Successful Requests** | ≥ 70% | Incluyendo fallbacks exitosos |
| **95th Percentile Response Time** | < 5000 ms | Tiempo de respuesta aceptable |
| **Mean Response Time** | < 2000 ms | Promedio bajo |

#### ⚠️ Señales de Problemas

| Métrica | Valor Problemático | Acción |
|---------|-------------------|---------|
| **Failed Requests** | > 30% | Circuit Breaker no está funcionando correctamente |
| **95th Percentile** | > 10000 ms | El sistema está saturado, revisar configuración |
| **Timeouts** | > 50% | Aumentar `timeoutDuration` en Resilience4j |

---

## 📊 Monitoreo con Actuator

### 1. Estado del Circuit Breaker

Mientras Gatling ejecuta, consulta el estado en otra terminal:

```bash
# Estado general de Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers | jq

# Estado específico de mongoService
curl http://localhost:8080/actuator/circuitbreakers/mongoService | jq
```

**Respuesta Esperada - CLOSED (Normal):**

```json
{
  "name": "mongoService",
  "state": "CLOSED",
  "metrics": {
    "failureRate": "15.5%",
    "slowCallRate": "0.0%",
    "numberOfSuccessfulCalls": 850,
    "numberOfFailedCalls": 150,
    "numberOfBufferedCalls": 1000
  }
}
```

**Respuesta Esperada - OPEN (Protegido):**

```json
{
  "name": "mongoService",
  "state": "OPEN",
  "metrics": {
    "failureRate": "65.0%",
    "numberOfNotPermittedCalls": 200,
    "stateTransition": "CLOSED_TO_OPEN"
  }
}
```

### 2. Eventos de Circuit Breaker

```bash
# Ver últimos eventos
curl http://localhost:8080/actuator/circuitbreakerevents | jq
```

**Eventos Clave:**

```json
{
  "circuitBreakerEvents": [
    {
      "circuitBreakerName": "mongoService",
      "type": "STATE_TRANSITION",
      "creationTime": "2025-10-14T15:23:45.123",
      "stateTransition": "CLOSED_TO_OPEN",
      "cause": "Failure rate threshold exceeded: 52%"
    },
    {
      "circuitBreakerName": "mongoService",
      "type": "NOT_PERMITTED",
      "creationTime": "2025-10-14T15:23:46.456"
    }
  ]
}
```

### 3. Métricas de Retry

```bash
# Estado de reintentos
curl http://localhost:8080/actuator/retries | jq

# Eventos de reintentos
curl http://localhost:8080/actuator/retryevents/mongoService | jq
```

**Eventos de Retry:**

```json
{
  "retryEvents": [
    {
      "retryName": "mongoService",
      "type": "RETRY",
      "creationTime": "2025-10-14T15:23:45.789",
      "numberOfAttempts": 1,
      "cause": "SocketTimeoutException: Connection timeout"
    },
    {
      "retryName": "mongoService",
      "type": "SUCCESS_AFTER_RETRY",
      "numberOfAttempts": 2
    }
  ]
}
```

---

## 📈 Interpretación de Métricas

### Escenario 1: Todo Funciona Correctamente

```
Circuit Breaker: CLOSED
Failure Rate: 5-15%
Retry Success Rate: 95%
Response Time P95: < 2000ms
```

**✅ Interpretación:** El sistema es resiliente. Los fallos transitorios se recuperan con Retry.

---

### Escenario 2: Circuit Breaker Activado

```
Circuit Breaker: OPEN → HALF_OPEN → CLOSED
Failure Rate: 60% → 40% → 10%
Fallback Calls: 500+
Response Time P95: 1500ms (mejora drástica)
```

**✅ Interpretación:** 
- El Circuit Breaker **protegió el sistema** al detectar fallos masivos
- Las llamadas fallidas se manejaron con **fallback** (carrito vacío)
- Los usuarios **no experimentaron timeouts** de 30 segundos
- El sistema se **recuperó automáticamente** al mejorar MongoDB

**📊 Gráfica Esperada en Gatling:**

```
Requests per Second
    ↑
100 |     ╱╲                 ← Pico de carga
 80 |    ╱  ╲    ╱╲          ← CB se abre aquí
 60 |   ╱    ╲  ╱  ╲         
 40 |  ╱      ╲╱    ╲___     ← Fallbacks manejando carga
 20 | ╱                  ╲
  0 |________________________→ Time
      0s  10s  20s  30s  40s

Response Time (ms)
    ↑
5000|        █                ← Antes de CB
3000|      █ █                
1000|  ████ █ ████            ← Después de CB (fallback rápido)
   0|________________________→ Time
```

---

### Escenario 3: Sistema Saturado (Sin Resilience4j)

**❌ Sin Circuit Breaker:**

```
Todos los threads bloqueados esperando MongoDB
Response Time P95: 30,000ms
Timeout Rate: 80%
Application CRASHED
```

**✅ Con Circuit Breaker:**

```
Circuit Breaker: OPEN
Fallback Calls: 100%
Response Time P95: 150ms (fallback instantáneo)
Application STABLE
```

---

## 🧪 Escenarios de Fallo Simulados

### Opción 1: Simular Caída de MongoDB

Para ver el Circuit Breaker en acción real:

```bash
# Detener MongoDB temporalmente (Windows)
net stop MongoDB

# Ejecutar la prueba de Gatling
mvn gatling:test -Dgatling.simulationClass=com.infinia.sports.performance.Resilience4jStressSimulation

# Observar logs del backend
# Verás: Circuit Breaker OPEN, fallbacks activados

# Reiniciar MongoDB
net start MongoDB

# Observar: Circuit Breaker HALF_OPEN → CLOSED (recuperación automática)
```

### Opción 2: Ajustar Umbrales para Pruebas Rápidas

Modifica temporalmente `application.properties` para ver el CB abrirse más rápido:

```properties
# Configuración TEMPORAL para pruebas
resilience4j.circuitbreaker.instances.mongoService.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.mongoService.failureRateThreshold=30
resilience4j.circuitbreaker.instances.mongoService.waitDurationInOpenState=10000
```

Esto hará que el Circuit Breaker se abra con:
- Solo 5 llamadas en lugar de 10
- 30% de fallos en lugar de 50%
- Reabrirse en 10 segundos en lugar de 60

---

## 📝 Checklist de Validación

Usa esta checklist para validar que Resilience4j funciona correctamente:

### ✅ Circuit Breaker

- [ ] El CB está en estado `CLOSED` cuando MongoDB funciona
- [ ] El CB se abre (`OPEN`) cuando MongoDB falla masivamente
- [ ] El método fallback se ejecuta cuando el CB está abierto
- [ ] El CB transiciona a `HALF_OPEN` después del tiempo configurado
- [ ] El CB vuelve a `CLOSED` si MongoDB se recupera

### ✅ Retry

- [ ] Se observan reintentos en los logs (`Retry attempt 1/3`)
- [ ] Los reintentos tienen espera exponencial (1s, 2s, 4s)
- [ ] Tras 3 intentos fallidos, se lanza la excepción al CB
- [ ] Las métricas de retry se exponen en Actuator

### ✅ Fallback

- [ ] El método fallback devuelve un carrito vacío
- [ ] No se lanza excepción 500 al usuario final
- [ ] Los logs muestran advertencia `⚠️ FALLBACK`
- [ ] La aplicación permanece estable bajo fallos

### ✅ Métricas

- [ ] Actuator expone `/actuator/circuitbreakers`
- [ ] Actuator expone `/actuator/circuitbreakerevents`
- [ ] Actuator expone `/actuator/retries`
- [ ] Las métricas se actualizan en tiempo real

---

## 🎓 Conclusión

Con estas pruebas has validado que:

1. **Circuit Breaker protege la aplicación** de cascadas de fallos
2. **Retry maneja fallos transitorios** sin que el usuario lo note
3. **Fallbacks proporcionan degradación elegante** en lugar de errores 500
4. **El sistema es resiliente** bajo carga extrema

### Próximos Pasos

- [ ] Ejecutar pruebas en entorno de staging
- [ ] Ajustar umbrales basándose en métricas reales de producción
- [ ] Implementar Rate Limiter si hay abuso de APIs
- [ ] Configurar alertas basadas en métricas de Resilience4j

---

## 📚 Referencias

- [Documentación Resilience4j](https://resilience4j.readme.io/)
- [Guía Completa Resilience4j](./resilience4j-guia-completa.md)
- [Documentación Actuator](./spring-boot-actuator.md)
- [Documentación Gatling](https://gatling.io/docs/current/)

---

**¿Preguntas o problemas?** Revisa los logs del backend y las métricas de Actuator para diagnosticar.
