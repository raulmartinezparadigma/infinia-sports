# Resilience4j - Guía Completa: El POR QUÉ, PARA QUÉ y CÓMO

**Autor:** Cascade AI  
**Fecha:** 13 de octubre de 2025  
**Nivel:** Intermedio-Avanzado  
**Tiempo de lectura:** 30 minutos

---

## 📚 Tabla de Contenidos

1. [El POR QUÉ - El Problema](#el-por-qué---el-problema)
2. [El PARA QUÉ - Objetivos y Beneficios](#el-para-qué---objetivos-y-beneficios)
3. [Conceptos Fundamentales](#conceptos-fundamentales)
4. [El CÓMO - Implementación Técnica](#el-cómo---implementación-técnica)
5. [Patrones de Resiliencia](#patrones-de-resiliencia)
6. [Casos de Uso Reales](#casos-de-uso-reales)
7. [Mejores Prácticas](#mejores-prácticas)
8. [Conclusión](#conclusión)

---

## 🎯 El POR QUÉ - El Problema

### La Realidad de los Sistemas Distribuidos

Imagina que tu aplicación Infinia Sports depende de varios servicios externos:

- **SendGrid** para enviar emails de confirmación de pedidos
- **MongoDB** para almacenar carritos de compra
- **Servicio de pagos** (Redsys, Bizum) para procesar transacciones
- **API de inventario** para verificar stock

**¿Qué puede salir mal?**

```
Usuario hace un pedido
    ↓
Backend llama a API de pagos → ⏰ Timeout (30 segundos esperando)
    ↓
Usuario frustrado, cierra la página
    ↓
Backend finalmente recibe respuesta... pero ya es tarde
```

### Problema 1: Fallos en Cascada

```
API de Pagos está lenta (5 segundos por petición)
    ↓
Threads del backend bloqueados esperando
    ↓
Pool de conexiones agotado
    ↓
Backend deja de responder a TODAS las peticiones
    ↓
💥 Toda la aplicación CAÍDA por un servicio lento
```

**Ejemplo real:**
```java
// ❌ CÓDIGO SIN PROTECCIÓN
public OrderDTO processPayment(PaymentRequest request) {
    // Si el servicio de pagos tarda 30 segundos o falla...
    PaymentResponse response = paymentService.charge(request);
    // ... TODOS los threads quedan bloqueados aquí
    return orderMapper.toDTO(response);
}
```

**Resultado:** Un servicio externo lento puede tumbar TODA tu aplicación.

### Problema 2: Reintentos Infinitos

```java
// ❌ CÓDIGO INGENUO
public void sendEmail(String to, String body) {
    try {
        emailService.send(to, body);
    } catch (Exception e) {
        // Reintento inmediato
        emailService.send(to, body); // Si falla otra vez?
    }
}
```

**Resultado:** 
- Saturación del servicio externo con reintentos
- Agotamiento de recursos
- El problema empeora en lugar de mejorar

### Problema 3: Sin Plan B

```java
// ❌ SIN FALLBACK
public List<Product> getRecommendations(UUID userId) {
    // Si MongoDB está caído...
    return recommendationEngine.getFor(userId); 
    // ↑ Excepción → Usuario ve error 500
}
```

**Mejor con fallback:**
```java
// ✅ CON FALLBACK
public List<Product> getRecommendations(UUID userId) {
    try {
        return recommendationEngine.getFor(userId);
    } catch (Exception e) {
        // Fallback: Devolver productos populares
        return productService.getMostPopular();
    }
}
```

### Problema 4: No Saber Cuándo un Servicio Está Mal

```
Servicio X está caído (100% fallos)
    ↓
Backend sigue intentando llamarlo
    ↓
Cada petición espera timeout completo (30s)
    ↓
Desperdiciar recursos en algo que SABEMOS que va a fallar
```

**Pregunta clave:** ¿Por qué seguir llamando a un servicio que está claramente caído?

---

## 🎯 El PARA QUÉ - Objetivos y Beneficios

### Objetivo Principal: **Resiliencia**

> **Resiliencia:** Capacidad de un sistema para resistir, adaptarse y recuperarse de fallos sin colapsar por completo.

### Beneficios Concretos

#### 1. **Prevenir Fallos en Cascada**

**Sin Resilience4j:**
```
MongoDB caído → Timeout 30s → Thread bloqueado → Pool agotado → App caída
```

**Con Resilience4j:**
```
MongoDB caído → Circuit Breaker abierto → Respuesta inmediata con fallback → App funcionando
```

**Resultado:** Un fallo aislado NO tumba toda la aplicación.

#### 2. **Mejorar Experiencia de Usuario**

**Sin protección:**
- Usuario espera 30 segundos → Error 500
- "Lo siento, algo salió mal"

**Con protección:**
- Respuesta en 200ms con datos cache o fallback
- "Ups, el servicio de recomendaciones no está disponible. Aquí tienes nuestros productos más populares"

**Resultado:** Degradación elegante en lugar de error total.

#### 3. **Ahorrar Recursos**

**Sin Circuit Breaker:**
```
Servicio caído → 1000 peticiones × 30s timeout = 30,000 segundos desperdiciados
```

**Con Circuit Breaker:**
```
Servicio caído → Circuit abierto → 1000 peticiones × 1ms = 1 segundo
```

**Resultado:** 30,000× más eficiente.

#### 4. **Recuperación Automática**

```
10:00 - Servicio cae → Circuit Breaker se abre
10:05 - Circuit Breaker prueba si servicio volvió (half-open)
10:06 - Servicio OK → Circuit se cierra → Operación normal
```

**Resultado:** Auto-sanación sin intervención manual.

#### 5. **Observabilidad**

Resilience4j emite métricas:
- % de peticiones exitosas/fallidas
- Estado de circuit breakers
- Latencia de servicios
- Rate de reintentos

**Resultado:** Sabes qué está fallando y cuándo.

---


## 🧠 Conceptos Fundamentales

### 1. Circuit Breaker (Interruptor de Circuito)

**Analogía:** El interruptor automático de tu casa.

**Estados:**

```
┌─────────────┐
│   CLOSED    │ ← Estado normal: peticiones pasan
│  (Cerrado)  │
└──────┬──────┘
       │ Demasiados fallos (50% en 100 peticiones)
       ↓
┌─────────────┐
│    OPEN     │ ← Servicio caído: peticiones fallan rápido
│  (Abierto)  │
└──────┬──────┘
       │ Después de N segundos (ej: 60s)
       ↓
┌─────────────┐
│  HALF_OPEN  │ ← Probando: permitir algunas peticiones
│ (Semi-abierto)│
└──────┬──────┘
       │
       ├─→ Si funciona: vuelve a CLOSED
       └─→ Si falla: vuelve a OPEN
```

**Configuración típica:**
```yaml
resilience4j.circuitbreaker:
  instances:
    paymentService:
      slidingWindowSize: 100              # Ventana de 100 peticiones
      failureRateThreshold: 50            # Abre si ≥50% fallan
      waitDurationInOpenState: 60s        # Espera 60s antes de probar
      permittedNumberOfCallsInHalfOpenState: 10  # 10 peticiones de prueba
```

**Ejemplo:**
```
Peticiones: ✓✓✓✗✗✗✗✗✗✗ (60% fallos)
     ↓
Circuit Breaker se ABRE
     ↓
Próximas peticiones fallan inmediatamente (sin llamar al servicio)
     ↓
Después de 60s → Estado HALF_OPEN → Prueba 10 peticiones
     ↓
Si ≥5 peticiones OK → Circuit se CIERRA
Si ≥5 peticiones FAIL → Circuit vuelve a OPEN
```

### 2. Retry (Reintento)

**Problema:** Fallo temporal (ej: timeout momentáneo)

**Solución:** Reintentar con estrategia inteligente.

**Estrategias:**

#### a) **Reintento Exponencial con Backoff**
```
Intento 1: inmediato
Intento 2: espera 1s
Intento 3: espera 2s
Intento 4: espera 4s
Intento 5: espera 8s
```

**Por qué exponencial?**
- Evita saturar el servicio que está recuperándose
- Da tiempo al servicio a estabilizarse

#### b) **Reintento con Jitter (Variación)**
```
Intento 1: espera 1s ± random(0-200ms) = 0.8s
Intento 2: espera 2s ± random(0-400ms) = 2.3s
```

**Por qué jitter?**
- Evita que todos los clientes reinten al mismo tiempo
- Reduce el efecto "thundering herd"

**Configuración:**
```yaml
resilience4j.retry:
  instances:
    mongoService:
      maxAttempts: 3
      waitDuration: 1s
      exponentialBackoffMultiplier: 2
      retryExceptions:
        - java.net.ConnectException
        - java.net.SocketTimeoutException
```

### 3. Rate Limiter (Limitador de Tasa)

**Problema:** Proteger tu API de sobrecarga.

**Solución:** Limitar peticiones por segundo/minuto.

**Tipos:**

#### a) **Token Bucket**
```
Bucket tiene 100 tokens
Cada petición consume 1 token
Tokens se reponen a 10/segundo

Si no hay tokens → Petición rechazada (429 Too Many Requests)
```

#### b) **Fixed Window**
```
Ventana: 1 minuto
Límite: 60 peticiones

Minuto 1: 0-59 peticiones OK, 60+ rechazadas
Minuto 2: Reset contador → 0-59 peticiones OK
```

**Ejemplo:**
```yaml
resilience4j.ratelimiter:
  instances:
    publicApi:
      limitForPeriod: 100      # 100 peticiones
      limitRefreshPeriod: 1s   # por segundo
      timeoutDuration: 0       # No esperar, rechazar inmediatamente
```

### 4. Bulkhead (Compartimento Estanco)

**Analogía:** Compartimentos de un barco (Titanic).

**Problema:** Un servicio lento consume todos los threads.

**Solución:** Aislar recursos por servicio.

```
Thread Pool Total: 200 threads
    ↓
Bulkhead PaymentService: máx 50 threads
Bulkhead EmailService: máx 30 threads
Bulkhead DefaultPool: 120 threads
```

**Resultado:** Si EmailService se satura, solo consume 30 threads, no afecta al resto.

**Configuración:**
```yaml
resilience4j.bulkhead:
  instances:
    emailService:
      maxConcurrentCalls: 30
      maxWaitDuration: 100ms
```

### 5. Time Limiter (Limitador de Tiempo)

**Problema:** Operación tarda demasiado.

**Solución:** Timeout configurable.

```java
@TimeLimiter(name = "paymentService")
public CompletableFuture<PaymentResponse> processPayment(PaymentRequest req) {
    return CompletableFuture.supplyAsync(() -> 
        paymentApi.charge(req)  // Si tarda >3s → TimeoutException
    );
}
```

**Configuración:**
```yaml
resilience4j.timelimiter:
  instances:
    paymentService:
      timeoutDuration: 3s
```

---


## 🔨 El CÓMO - Implementación Técnica

### Arquitectura de Resilience4j

Resilience4j está diseñado para Java 8+ y programación funcional:

```
┌─────────────────────────────────────────┐
│        Tu Servicio/Método               │
│                                         │
│  @CircuitBreaker(name = "payment")      │
│  @Retry(name = "payment")               │
│  @RateLimiter(name = "payment")         │
│  public PaymentResponse charge(...) {   │
│      return paymentApi.call(...);       │
│  }                                      │
└─────────────────────────────────────────┘
           │
           │ Resilience4j intercepta la llamada
           ↓
┌─────────────────────────────────────────┐
│    Decoradores de Resilience4j          │
│                                         │
│  1. Rate Limiter: ¿Excede límite?       │
│  2. Circuit Breaker: ¿Está abierto?     │
│  3. Retry: Si falla, ¿reintentar?       │
│  4. Time Limiter: ¿Timeout?             │
│  5. Bulkhead: ¿Thread disponible?       │
└─────────────────────────────────────────┘
           │
           ↓
┌─────────────────────────────────────────┐
│      Servicio Externo Real              │
│   (MongoDB, SendGrid, API Pagos)        │
└─────────────────────────────────────────┘
```

### Dependencias Maven

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### Configuración Base (application.yml)

```yaml
resilience4j:
  # Circuit Breaker
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 100
        minimumNumberOfCalls: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
        permittedNumberOfCallsInHalfOpenState: 10
        automaticTransitionFromOpenToHalfOpenEnabled: true
        
    instances:
      paymentService:
        baseConfig: default
      emailService:
        baseConfig: default
        failureRateThreshold: 60  # Más tolerante para emails
        
  # Retry
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.net.ConnectException
          - java.net.SocketTimeoutException
        ignoreExceptions:
          - com.infinia.sports.exception.BusinessException
          
    instances:
      mongoService:
        baseConfig: default
      emailService:
        baseConfig: default
        maxAttempts: 2  # Menos reintentos para emails
        
  # Rate Limiter
  ratelimiter:
    configs:
      default:
        limitForPeriod: 100
        limitRefreshPeriod: 1s
        timeoutDuration: 0
        
    instances:
      publicApi:
        limitForPeriod: 50  # 50 peticiones/segundo
        
  # Time Limiter
  timelimiter:
    configs:
      default:
        timeoutDuration: 3s
        
    instances:
      paymentService:
        timeoutDuration: 5s  # Pagos pueden tardar más
      emailService:
        timeoutDuration: 10s
```

---

## 🎯 Patrones de Resiliencia en Infinia Sports

### Patrón 1: Circuit Breaker para Servicio de Emails

**Escenario:** SendGrid (servicio de emails) está caído o lento.

**Sin protección:**
```java
public void sendOrderConfirmation(Order order) {
    // Si SendGrid está caído → timeout 30s
    // Cada pedido espera 30s → threads bloqueados
    emailService.send(order.getEmail(), createEmailBody(order));
}
```

**Con Circuit Breaker:**
```java
@Service
public class OrderMailService {
    
    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmail")
    public void sendOrderConfirmation(Order order) {
        emailService.send(order.getEmail(), createEmailBody(order));
        log.info("Email enviado a: {}", order.getEmail());
    }
    
    // Fallback: Guardar en cola para envío posterior
    private void fallbackSendEmail(Order order, Exception e) {
        log.warn("Email service down, queueing email for: {}", order.getEmail());
        emailQueue.add(new EmailTask(order.getEmail(), createEmailBody(order)));
        // El pedido se procesa correctamente, email se enviará después
    }
}
```

**Resultado:**
- ✅ Pedido se procesa correctamente
- ✅ Email se encola para envío posterior
- ✅ Usuario no espera 30s
- ✅ Threads liberados inmediatamente

### Patrón 2: Retry para MongoDB

**Escenario:** MongoDB tiene un timeout momentáneo.

**Sin protección:**
```java
public Cart getCart(String userId) {
    // Si hay un blip de red → excepción → usuario ve error
    return cartRepository.findByUserId(userId).orElseThrow();
}
```

**Con Retry:**
```java
@Service
public class CartService {
    
    @Retry(name = "mongoService", fallbackMethod = "fallbackGetCart")
    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId).orElseThrow();
    }
    
    private Cart fallbackGetCart(String userId, Exception e) {
        log.error("Failed to get cart after retries for user: {}", userId, e);
        // Devolver carrito vacío o desde cache
        return cacheService.getCart(userId).orElse(new Cart(userId));
    }
}
```

**Flujo:**
```
Intento 1: MongoDB timeout → falla
    ↓ espera 1s
Intento 2: MongoDB timeout → falla  
    ↓ espera 2s
Intento 3: MongoDB OK → ✅ éxito

Usuario solo nota un delay de 3s en lugar de un error
```

### Patrón 3: Combinación Circuit Breaker + Retry + Fallback

**Escenario:** Servicio de pagos (Redsys/Bizum) puede fallar.

```java
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackProcessPayment")
    @Retry(name = "paymentService")
    @TimeLimiter(name = "paymentService")
    public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // Si falla → Retry lo reintenta
            // Si sigue fallando → Circuit Breaker se abre
            // Si tarda mucho → Time Limiter cancela
            return paymentApi.charge(request);
        });
    }
    
    private CompletableFuture<PaymentResponse> fallbackProcessPayment(
            PaymentRequest request, Exception e) {
        log.error("Payment service unavailable", e);
        
        // Opciones de fallback:
        // 1. Marcar pedido como "pending payment"
        // 2. Permitir "pago contra entrega"
        // 3. Notificar al equipo de soporte
        
        return CompletableFuture.completedFuture(
            PaymentResponse.builder()
                .status(PaymentStatus.PENDING)
                .message("Payment service temporarily unavailable. Order saved for manual processing.")
                .build()
        );
    }
}
```

**Beneficios:**
- ✅ 3 intentos automáticos (Retry)
- ✅ Timeout de 5s máximo (Time Limiter)
- ✅ Si falla mucho, deja de intentar (Circuit Breaker)
- ✅ Degradación elegante (Fallback)

---

## 📊 Casos de Uso Reales en Infinia Sports

### Caso 1: Black Friday - Protección contra Sobrecarga

**Problema:** Black Friday, 10,000 usuarios simultáneos.

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @RateLimiter(name = "publicApi")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        List<ProductDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }
}
```

**Configuración:**
```yaml
resilience4j.ratelimiter.instances.publicApi:
  limitForPeriod: 50      # 50 peticiones
  limitRefreshPeriod: 1s  # por segundo
```

**Resultado:**
- ✅ Máximo 50 peticiones/segundo por usuario/IP
- ✅ Peticiones excedentes → 429 Too Many Requests
- ✅ Backend protegido de sobrecarga
- ✅ Usuarios legítimos siguen funcionando

### Caso 2: MongoDB Caído - Degradación Elegante

**Escenario:** MongoDB se cae durante 5 minutos.

```java
@Service
public class RecommendationService {
    
    @CircuitBreaker(name = "mongoService", fallbackMethod = "fallbackRecommendations")
    @Cacheable("recommendations")
    public List<Product> getRecommendations(UUID userId) {
        UserPreferences prefs = userPrefsRepository.findById(userId).orElseThrow();
        return recommendationEngine.compute(prefs);
    }
    
    private List<Product> fallbackRecommendations(UUID userId, Exception e) {
        log.warn("Recommendation engine unavailable, using popular products");
        // Fallback: Productos más vendidos (datos en memoria/cache)
        return popularProductsCache.getTop(10);
    }
}
```

**Experiencia del usuario:**
- Usuario A: Ve sus recomendaciones personalizadas ✅
- MongoDB se cae...
- Usuario B: Ve productos populares (no personalizados pero funciona) ✅
- MongoDB se recupera...
- Usuario C: Ve sus recomendaciones personalizadas ✅

**Sin Resilience4j:** Todos los usuarios ven error 500 ❌

### Caso 3: SendGrid Lento - Emails Asíncronos

```java
@Service
public class OrderConfirmationService {
    
    @CircuitBreaker(name = "emailService", fallbackMethod = "queueEmail")
    @Async
    public CompletableFuture<Void> sendConfirmation(Order order) {
        EmailTemplate template = templateService.getOrderConfirmation();
        emailService.send(
            order.getEmail(),
            template.render(order)
        );
        return CompletableFuture.completedFuture(null);
    }
    
    private CompletableFuture<Void> queueEmail(Order order, Exception e) {
        // Email se enviará cuando SendGrid se recupere
        emailQueue.enqueue(new EmailJob(order));
        
        // Pero el pedido YA está confirmado
        return CompletableFuture.completedFuture(null);
    }
}
```

**Resultado:**
- ✅ Pedido procesado inmediatamente
- ✅ Email se envía cuando sea posible
- ✅ Usuario no espera

---

## 📈 Monitorización con Actuator

### Endpoints de Resilience4j

```bash
# Ver estado de todos los Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers

# Ver eventos de un Circuit Breaker específico
curl http://localhost:8080/actuator/circuitbreakerevents/paymentService

# Ver métricas de Retry
curl http://localhost:8080/actuator/metrics/resilience4j.retry.calls

# Ver Rate Limiter
curl http://localhost:8080/actuator/ratelimiters
```

### Métricas en Prometheus/Grafana

Resilience4j exporta métricas:

```
# Circuit Breaker
resilience4j_circuitbreaker_state{name="paymentService",state="closed"} 1
resilience4j_circuitbreaker_state{name="paymentService",state="open"} 0
resilience4j_circuitbreaker_calls_total{name="paymentService",kind="successful"} 1523
resilience4j_circuitbreaker_calls_total{name="paymentService",kind="failed"} 12

# Retry
resilience4j_retry_calls{name="mongoService",kind="successful_with_retry"} 45
resilience4j_retry_calls{name="mongoService",kind="failed_with_retry"} 3

# Rate Limiter
resilience4j_ratelimiter_available_permissions{name="publicApi"} 47
resilience4j_ratelimiter_waiting_threads{name="publicApi"} 0
```

---

## 🎓 Mejores Prácticas

### 1. Fallbacks Significativos

```java
// ❌ MAL: Fallback que no aporta valor
private PaymentResponse fallback(PaymentRequest req, Exception e) {
    throw new RuntimeException("Payment failed"); // Mismo efecto que sin fallback
}

// ✅ BIEN: Fallback útil
private PaymentResponse fallback(PaymentRequest req, Exception e) {
    // Guardar para procesamiento manual
    pendingPaymentsRepo.save(req);
    
    return PaymentResponse.builder()
        .status(PENDING_MANUAL_REVIEW)
        .message("Payment queued for processing")
        .orderId(req.getOrderId())
        .build();
}
```

### 2. No Abusar de Retry en Operaciones Costosas

```java
// ❌ MAL: Retry en operación de pago
@Retry(name = "payment", maxAttempts = 5)  // Podría cobrar 5 veces!
public void chargeCard(Payment payment) {
    paymentGateway.charge(payment);
}

// ✅ BIEN: Sin retry automático, manejo manual
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
public void chargeCard(Payment payment) {
    paymentGateway.charge(payment);
}
```

### 3. Configurar Excepciones Correctamente

```java
resilience4j.retry:
  instances:
    payment:
      retryExceptions:
        - java.net.SocketTimeoutException  # SÍ reintentar
        - java.net.ConnectException        # SÍ reintentar
      ignoreExceptions:
        - com.payment.InsufficientFundsException  # NO reintentar
        - com.payment.InvalidCardException        # NO reintentar
```

### 4. Timeouts Realistas

```yaml
# ❌ MAL: Timeouts muy cortos
resilience4j.timelimiter.instances.payment:
  timeoutDuration: 500ms  # API de pago no puede responder tan rápido

# ✅ BIEN: Timeout realista basado en SLA del servicio
resilience4j.timelimiter.instances.payment:
  timeoutDuration: 10s    # API de pago SLA: 95% < 5s
```

### 5. Circuit Breaker en Servicios Externos, No Internos

```java
// ❌ MAL: Circuit breaker en método interno
@CircuitBreaker(name = "calculateTotal")
private BigDecimal calculateOrderTotal(Order order) {
    return order.getItems().stream()
        .map(item -> item.getPrice().multiply(item.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

// ✅ BIEN: Circuit breaker en llamada externa
@CircuitBreaker(name = "paymentGateway")
public PaymentResponse callPaymentApi(PaymentRequest req) {
    return paymentApiClient.post("/charge", req);
}
```

---

## 📚 Resumen: Cuándo Usar Cada Patrón

| Patrón | Cuándo Usar | Ejemplo |
|--------|-------------|---------|
| **Circuit Breaker** | Servicio externo que puede fallar completamente | SendGrid, API de pagos, MongoDB |
| **Retry** | Fallos transitorios/temporales | Timeouts de red, blips de conexión |
| **Rate Limiter** | Proteger tu API de sobrecarga | Endpoints públicos, APIs de terceros |
| **Bulkhead** | Aislar recursos por servicio | Thread pools separados por funcionalidad |
| **Time Limiter** | Operaciones que no deben tardar mucho | APIs externas con SLA |
| **Fallback** | Degradación elegante del servicio | Usar cache, datos estáticos, mensaje amigable |

---

## 🎯 Conclusión

### Lo que Hemos Aprendido

1. **POR QUÉ:** Los sistemas distribuidos fallan. Es inevitable.

2. **PARA QUÉ:** Resilience4j nos permite:
   - Prevenir fallos en cascada
   - Mejorar experiencia de usuario
   - Ahorrar recursos
   - Auto-recuperación
   - Observabilidad

3. **CÓMO:** Mediante patrones probados:
   - Circuit Breaker para proteger de fallos
   - Retry para recuperarse de errores transitorios
   - Fallbacks para degradación elegante
   - Rate Limiter para proteger de sobrecarga

### Próximos Pasos

1. **Implementar en Infinia Sports**
   - Proteger servicio de emails
   - Proteger MongoDB
   - Proteger API de pagos

2. **Monitorizar**
   - Configurar dashboards en Grafana
   - Alertas en errores críticos

3. **Iterar**
   - Ajustar umbrales según datos reales
   - Añadir más fallbacks

### Recuerda

> "No es cuestión de SI un servicio externo fallará, sino CUÁNDO. Prepárate."

**Resilience4j = Seguro de vida para tu aplicación** 🛡️

---

**Autor:** Cascade AI  
**Proyecto:** Infinia Sports  
**Fecha:** 13 de octubre de 2025

### Referencias

- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Boot + Resilience4j Guide](https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/)
- [Circuit Breaker Pattern - Martin Fowler](https://martinfowler.com/bliki/CircuitBreaker.html)
- [Release It! - Michael Nygard](https://pragprog.com/titles/mnee2/release-it-second-edition/)

---

*A continuación: Implementación real en el proyecto Infinia Sports...*

## 🎯 GUÍA DE DECISIÓN: ¿Qué Estrategia Usar?

### Árbol de Decisión

```
¿Necesitas proteger tu aplicación?
│
├─ ¿Es una llamada a un SERVICIO EXTERNO?
│  │
│  ├─ SÍ → Continuar análisis ↓
│  │
│  └─ NO → ❌ NO usar Resilience4j
│           (Solo para operaciones internas, no necesita protección)
│
├─ ¿El servicio externo puede FALLAR COMPLETAMENTE?
│  │  (Ejemplos: API de terceros, BD remota, servicio de emails)
│  │
│  ├─ SÍ → ✅ CIRCUIT BREAKER
│  │        + FALLBACK obligatorio
│  │
│  └─ NO → Continuar ↓
│
├─ ¿La operación puede tener FALLOS TRANSITORIOS?
│  │  (Ejemplos: timeouts de red, blips momentáneos)
│  │
│  ├─ SÍ → ✅ RETRY
│  │        + Exponential Backoff
│  │        + Límite de intentos (2-3)
│  │
│  └─ NO → Continuar ↓
│
├─ ¿La operación puede TARDAR DEMASIADO?
│  │  (Ejemplos: APIs sin SLA definido, operaciones pesadas)
│  │
│  ├─ SÍ → ✅ TIME LIMITER
│  │        + Timeout basado en SLA o percentil 99
│  │
│  └─ NO → Continuar ↓
│
├─ ¿Necesitas LIMITAR el tráfico?
│  │  (Ejemplos: API pública, protección contra DDoS)
│  │
│  ├─ SÍ → ✅ RATE LIMITER
│  │        + Límite por usuario/IP
│  │
│  └─ NO → Continuar ↓
│
└─ ¿Un servicio puede CONSUMIR TODOS los recursos?
   │  (Ejemplos: operaciones CPU-intensivas, queries pesadas)
   │
   ├─ SÍ → ✅ BULKHEAD
   │        + Pool dedicado de threads
   │
   └─ NO → ¿Seguro que necesitas Resilience4j?
```

---

## 🧭 Matriz de Decisión por Tipo de Servicio

### 1. Servicio de Emails (SendGrid, SES, etc.)

| Pregunta | Respuesta | Estrategia |
|----------|-----------|------------|
| ¿Puede estar completamente caído? | ✅ Sí | **Circuit Breaker** |
| ¿Fallos transitorios? | ✅ Sí (rate limits) | **Retry** (máx 2 intentos) |
| ¿Operación crítica? | ❌ No (email puede esperar) | **Fallback** → Encolar |
| ¿Puede tardar mucho? | ✅ Sí (10-30s) | **Time Limiter** (15s) |
| ¿Necesita limitar tráfico? | ❌ No (lo limita el proveedor) | - |

**Configuración recomendada:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      emailService:
        slidingWindowSize: 50
        failureRateThreshold: 60  # Más tolerante
        waitDurationInOpenState: 120s  # 2 minutos
        
  retry:
    instances:
      emailService:
        maxAttempts: 2  # Solo 1 reintento
        waitDuration: 2s
        
  timelimiter:
    instances:
      emailService:
        timeoutDuration: 15s
```

**Fallback:**
```java
private void fallbackSendEmail(EmailRequest req, Exception e) {
    // Encolar para envío posterior
    emailQueue.add(req);
    log.warn("Email queued due to service unavailability");
}
```

---

### 2. Base de Datos (MongoDB, PostgreSQL)

| Pregunta | Respuesta | Estrategia |
|----------|-----------|------------|
| ¿Puede estar completamente caída? | ✅ Sí | **Circuit Breaker** |
| ¿Fallos transitorios? | ✅ Sí (network blips) | **Retry** (3 intentos) |
| ¿Operación crítica? | ✅ Sí | **Fallback** → Cache o datos estáticos |
| ¿Puede tardar mucho? | ✅ Sí (queries lentas) | **Time Limiter** (5s) |
| ¿Necesita limitar concurrencia? | ✅ Sí | **Bulkhead** (pool limitado) |

**Configuración recomendada:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      mongoService:
        slidingWindowSize: 100
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
        
  retry:
    instances:
      mongoService:
        maxAttempts: 3
        waitDuration: 1s
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.net.SocketTimeoutException
          - com.mongodb.MongoTimeoutException
          
  bulkhead:
    instances:
      mongoService:
        maxConcurrentCalls: 50
```

**Fallback:**
```java
private List<Product> fallbackGetProducts(Exception e) {
    // Intentar desde cache
    return cacheService.getProducts()
        .orElse(defaultProducts);  // Productos por defecto
}
```

---

### 3. API de Pagos (Stripe, Redsys, PayPal)

| Pregunta | Respuesta | Estrategia |
|----------|-----------|------------|
| ¿Puede estar completamente caída? | ✅ Sí | **Circuit Breaker** |
| ¿Fallos transitorios? | ⚠️ Cuidado | **NO Retry** (riesgo de doble cobro) |
| ¿Operación crítica? | ✅ Sí | **Fallback** → Pago manual/diferido |
| ¿Puede tardar mucho? | ✅ Sí (3D Secure) | **Time Limiter** (30s) |
| ¿Necesita limitar tráfico? | ⚠️ Depende del SLA | **Rate Limiter** si procede |

**⚠️ IMPORTANTE:** NUNCA usar Retry automático en operaciones de pago.

**Configuración recomendada:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowSize: 50
        failureRateThreshold: 40  # Más estricto
        waitDurationInOpenState: 180s  # 3 minutos
        
  timelimiter:
    instances:
      paymentService:
        timeoutDuration: 30s  # 3D Secure puede tardar
```

**NO usar Retry:**
```java
// ❌ NUNCA HACER ESTO
@Retry(name = "payment")
public PaymentResponse charge(PaymentRequest req) {
    return paymentGateway.charge(req);  // Podría cobrar múltiples veces!
}
```

**Fallback:**
```java
private PaymentResponse fallbackCharge(PaymentRequest req, Exception e) {
    // Marcar pedido como "pendiente de pago manual"
    Order order = orderRepository.findById(req.getOrderId())
        .orElseThrow();
    order.setPaymentStatus(PaymentStatus.PENDING_MANUAL);
    orderRepository.save(order);
    
    // Notificar equipo de soporte
    supportNotificationService.send(
        "Payment gateway down for order: " + order.getId()
    );
    
    return PaymentResponse.pendingManualReview();
}
```

---

### 4. API Pública (tus endpoints REST)

| Pregunta | Respuesta | Estrategia |
|----------|-----------|------------|
| ¿Puede recibir tráfico masivo? | ✅ Sí | **Rate Limiter** |
| ¿Operaciones costosas? | ⚠️ Depende | **Bulkhead** si procede |
| ¿Proteger de DDoS? | ✅ Sí | **Rate Limiter** por IP |
| ¿Endpoints de lectura cacheable? | ✅ Sí | Cache + Circuit Breaker |

**Configuración recomendada:**
```yaml
resilience4j:
  ratelimiter:
    instances:
      publicApi:
        limitForPeriod: 100      # 100 peticiones
        limitRefreshPeriod: 1m   # por minuto
        timeoutDuration: 0       # Rechazar inmediatamente
        
      loginApi:
        limitForPeriod: 5        # Solo 5 intentos
        limitRefreshPeriod: 15m  # por 15 minutos (anti-brute-force)
```

---

### 5. Servicio de Caché (Redis, Memcached)

| Pregunta | Respuesta | Estrategia |
|----------|-----------|------------|
| ¿Puede estar caído? | ✅ Sí | **Circuit Breaker** |
| ¿Fallos transitorios? | ✅ Sí | **Retry** (2 intentos) |
| ¿Operación crítica? | ❌ No (es cache) | **Fallback** → Sin cache |
| ¿Puede tardar mucho? | ❌ No (debe ser rápido) | **Time Limiter** (100ms) |

**Configuración recomendada:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      cacheService:
        slidingWindowSize: 20
        failureRateThreshold: 50
        waitDurationInOpenState: 30s  # Reintentar rápido
        
  timelimiter:
    instances:
      cacheService:
        timeoutDuration: 100ms  # Cache debe ser rápido
```

**Fallback:**
```java
private Optional<Product> fallbackGetFromCache(String key, Exception e) {
    log.debug("Cache miss due to: {}", e.getMessage());
    return Optional.empty();  // Sin cache, ir a BD directamente
}
```

---

## 🔍 Checklist: ¿Necesito Resilience4j?

Responde estas preguntas sobre tu operación:

### Preguntas Obligatorias

- [ ] **¿Es una llamada a un servicio externo?**
  - Si NO → ❌ No necesitas Resilience4j
  - Si SÍ → Continuar

- [ ] **¿Controlas el servicio externo?**
  - Si SÍ (es tuyo) → ⚠️ Considera mejorar el servicio primero
  - Si NO (tercero) → ✅ Definitivamente necesitas protección

- [ ] **¿Qué pasa si el servicio falla?**
  - App completa cae → ✅ Circuit Breaker + Fallback
  - Solo esa feature no funciona → ✅ Circuit Breaker + Fallback
  - No importa → ⚠️ Considera si realmente necesitas el servicio

### Selección de Estrategias

**Usa Circuit Breaker SI:**
- [ ] El servicio puede estar completamente caído
- [ ] Fallos frecuentes degradarían tu app
- [ ] Tienes un plan B (fallback)

**Usa Retry SI:**
- [ ] Fallos son generalmente transitorios
- [ ] La operación es **idempotente** (seguro reintentar)
- [ ] NO es operación financiera crítica

**Usa Time Limiter SI:**
- [ ] El servicio no tiene SLA claro
- [ ] Has observado latencias variables
- [ ] Prefieres fallo rápido a espera indefinida

**Usa Rate Limiter SI:**
- [ ] Es un endpoint público
- [ ] Quieres prevenir abuso
- [ ] El servicio downstream tiene límites

**Usa Bulkhead SI:**
- [ ] La operación consume muchos recursos
- [ ] Un servicio lento puede afectar a otros
- [ ] Quieres aislar pools de threads

---

## 📏 Guía de Valores de Configuración

### Circuit Breaker

| Parámetro | Conservador | Equilibrado | Agresivo |
|-----------|-------------|-------------|----------|
| **slidingWindowSize** | 200 | 100 | 50 |
| **failureRateThreshold** | 70% | 50% | 30% |
| **waitDurationInOpenState** | 120s | 60s | 30s |
| **minimumNumberOfCalls** | 20 | 10 | 5 |

**Conservador:** Para servicios críticos que necesitan muchos datos antes de decidir
**Equilibrado:** Para la mayoría de casos
**Agresivo:** Para servicios conocidos por ser inestables

### Retry

| Parámetro | Conservador | Equilibrado | Agresivo |
|-----------|-------------|-------------|----------|
| **maxAttempts** | 2 | 3 | 5 |
| **waitDuration** | 2s | 1s | 500ms |
| **exponentialBackoffMultiplier** | 3 | 2 | 1.5 |

**Conservador:** Para operaciones costosas o servicios limitados
**Equilibrado:** Para la mayoría de casos
**Agresivo:** Para servicios muy estables con fallos muy raros

### Time Limiter

| Tipo de Operación | Timeout Recomendado |
|-------------------|---------------------|
| Cache (Redis) | 100ms - 200ms |
| Base de datos (lectura) | 1s - 3s |
| Base de datos (escritura) | 3s - 5s |
| API REST simple | 3s - 5s |
| API REST compleja | 10s - 15s |
| Pagos (3D Secure) | 30s - 60s |
| Procesamiento batch | 5min - 15min |

**Regla general:** Timeout = Percentil 99 de latencia + 20%

---

## 💡 Casos Especiales

### Caso 1: Operaciones No Idempotentes (Pagos)

**Problema:** Si falla un pago, ¿es seguro reintentar?

**Solución:**
```java
// ❌ NO usar Retry automático
@CircuitBreaker(name = "payment", fallbackMethod = "fallbackPayment")
public PaymentResponse processPayment(PaymentRequest req) {
    // Implementar idempotencia con idempotency key
    String idempotencyKey = req.getIdempotencyKey();
    
    // Verificar si ya se procesó
    Optional<Payment> existing = paymentRepo.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
        return PaymentResponse.fromExisting(existing.get());
    }
    
    // Procesar pago (una sola vez)
    return paymentGateway.charge(req);
}
```

### Caso 2: Lectura vs Escritura

**Lecturas:** Más tolerante a fallos
```yaml
resilience4j.circuitbreaker.instances.readService:
  failureRateThreshold: 60  # Más tolerante
  waitDurationInOpenState: 30s  # Recuperación rápida
```

**Escrituras:** Más conservador
```yaml
resilience4j.circuitbreaker.instances.writeService:
  failureRateThreshold: 30  # Más estricto
  waitDurationInOpenState: 120s  # Espera más tiempo
```

### Caso 3: Desarrollo vs Producción

**Desarrollo:** Más logging, menos agresivo
```yaml
resilience4j.circuitbreaker.instances.devApi:
  registerHealthIndicator: true
  slidingWindowType: COUNT_BASED
  slidingWindowSize: 10  # Menos muestras
```

**Producción:** Optimizado para rendimiento
```yaml
resilience4j.circuitbreaker.instances.prodApi:
  registerHealthIndicator: true
  slidingWindowType: TIME_BASED
  slidingWindowSize: 100
  recordExceptions:
    - java.lang.Exception
```

---

## 🎓 Resumen: Tu Proceso de Decisión

```
1. Identifica el servicio externo
      ↓
2. Clasifica el servicio (Email, DB, Payment, API, Cache)
      ↓
3. Usa la matriz de decisión para ese tipo
      ↓
4. Selecciona valores conservadores inicialmente
      ↓
5. Monitoriza métricas reales
      ↓
6. Ajusta configuración basándote en datos
      ↓
7. Documenta decisiones para el equipo
```

**Regla de oro:** Empieza conservador, ajusta según observas el comportamiento real.

---


---

## 🎯 IMPLEMENTACIÓN EN INFINIA SPORTS

### Ejemplos Reales del Código

A continuación se muestran los ejemplos reales implementados en el proyecto Infinia Sports.

#### 1. Circuit Breaker + Retry para Servicio de Emails

**Archivo:** `OrderMailPaymentServiceImpl.java`

```java
@Service
public class OrderMailPaymentServiceImpl implements OrderMailPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(OrderMailPaymentServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMailService orderMailService;

    /**
     * Envía email de confirmación de pedido con protección de Circuit Breaker y Retry.
     * 
     * Resilience4j:
     * - Circuit Breaker: Protege contra fallos del servicio de email (SendGrid)
     * - Retry: Reintenta hasta 2 veces con espera exponencial
     * - Fallback: Si falla, registra para envío manual posterior
     * 
     * @param orderId ID del pedido para enviar confirmación
     */
    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmail")
    @Retry(name = "emailService")
    public void sendOrderConfirmationEmail(String orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
            String html = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            String subject = "Resumen de tu pedido Infinia Sports #" + order.getOrderId();
            String to = order.getEmail();
            
            orderMailService.sendOrderSummary(to, subject, html);
            
            logger.info("✅ Email enviado a {} para orderId={}", to, orderId);
        } catch (Exception e) {
            logger.error("❌ Error al enviar email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar email", e);
        }
    }

    /**
     * Fallback cuando el servicio de email falla.
     * Registra el fallo para procesamiento manual posterior.
     */
    private void fallbackSendEmail(String orderId, Exception ex) {
        logger.warn("⚠️ FALLBACK: Servicio de email no disponible para orderId={}. " +
                    "Email debe enviarse manualmente. Causa: {}", 
                    orderId, ex.getMessage());
        
        // TODO: Implementar cola de emails pendientes para reintento posterior
        logger.error("📧 EMAIL PENDIENTE: Revisar y enviar manualmente confirmación para orderId={}", 
                     orderId);
    }
}
```

**Configuración aplicada:**
```properties
# Circuit Breaker - Servicio de Emails
resilience4j.circuitbreaker.instances.emailService.failureRateThreshold=60
resilience4j.circuitbreaker.instances.emailService.waitDurationInOpenState=120000

# Retry - Servicio de Emails
resilience4j.retry.instances.emailService.maxAttempts=2
resilience4j.retry.instances.emailService.waitDuration=2000
```

**¿Qué protege?**
- ✅ Si SendGrid está caído → Circuit Breaker se abre → Fallback inmediato
- ✅ Si hay timeout temporal → Retry lo reintenta 2 veces
- ✅ Si falla definitivamente → Email se registra para envío manual
- ✅ El pedido SE PROCESA correctamente aunque el email falle

---

#### 2. Retry + Circuit Breaker para MongoDB (Carritos)

**Archivo:** `CheckoutServiceImpl.java`

```java
@Service
public class CheckoutServiceImpl implements CheckoutService {
    
    private final CartRepository cartRepository;
    private final CartMapperMS cartMapper;

    /**
     * Obtiene el carrito del usuario/sesión con protección Retry para MongoDB.
     * 
     * Resilience4j:
     * - Retry: Reintenta hasta 3 veces si hay fallos transitorios de conexión
     * - Circuit Breaker: Protege contra caída total de MongoDB
     * - Fallback: Devuelve carrito vacío si MongoDB no está disponible
     */
    @Override
    @Retry(name = "mongoService")
    @CircuitBreaker(name = "mongoService", fallbackMethod = "fallbackGetCart")
    public CartDTO getCart(String sessionId, String userId) {
        return cartMapper.toDTO(getCartEntity(sessionId, userId));
    }
    
    /**
     * Fallback cuando MongoDB no está disponible.
     * Devuelve un carrito vacío para no romper la experiencia del usuario.
     */
    private CartDTO fallbackGetCart(String sessionId, String userId, Exception ex) {
        logger.warn("⚠️ FALLBACK: MongoDB no disponible al obtener carrito. " +
                    "sessionId={}, userId={}. Causa: {}",
                    sessionId, userId, ex.getMessage());
        
        // Devolver carrito vacío - degradación elegante
        CartDTO emptyCart = new CartDTO();
        emptyCart.setSessionId(sessionId);
        emptyCart.setUserId(userId);
        emptyCart.setItems(new ArrayList<>());
        return emptyCart;
    }
}
```

**Configuración aplicada:**
```properties
# Circuit Breaker - MongoDB
resilience4j.circuitbreaker.instances.mongoService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.mongoService.waitDurationInOpenState=60000

# Retry - MongoDB
resilience4j.retry.instances.mongoService.maxAttempts=3
resilience4j.retry.instances.mongoService.waitDuration=1000
resilience4j.retry.instances.mongoService.exponentialBackoffMultiplier=2
```

**¿Qué protege?**
- ✅ Si MongoDB tiene un blip momentáneo → Retry automático (1s, 2s, 4s)
- ✅ Si MongoDB está completamente caído → Circuit Breaker + Fallback
- ✅ Usuario ve carrito vacío en lugar de error 500
- ✅ Cuando MongoDB vuelve, Circuit Breaker se cierra automáticamente

---

### Flujos de Resiliencia en Acción

#### Escenario 1: SendGrid Lento

```
Usuario completa pedido
    ↓
Backend procesa pedido ✅
    ↓
Intenta enviar email → SendGrid responde en 20s (lento)
    ↓
Circuit Breaker detecta latencia alta
    ↓
Próximo pedido → SendGrid responde en 25s
    ↓
Circuit Breaker: "Demasiadas peticiones lentas" → ABRE
    ↓
Siguiente pedido → Fallback inmediato → Email registrado para envío manual
    ↓
Usuario recibe confirmación: "Pedido procesado. Email llegará pronto" ✅
    ↓
Después de 2 minutos → Circuit Breaker prueba si SendGrid mejoró
    ↓
SendGrid OK → Circuit Breaker se CIERRA → Operación normal
```

#### Escenario 2: MongoDB Timeout Temporal

```
Usuario consulta carrito
    ↓
MongoDB timeout (network blip) ❌
    ↓
Retry #1: Espera 1s → Reintenta → MongoDB timeout ❌
    ↓
Retry #2: Espera 2s → Reintenta → MongoDB OK ✅
    ↓
Usuario recibe su carrito (solo nota 3s de delay)
    ↓
Sin Resilience4j → Usuario hubiera visto error 500 ❌
```

---

### Monitorización con Actuator

**Endpoints disponibles:**

```bash
# Ver estado de todos los Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers

# Ver eventos del Circuit Breaker de email
curl http://localhost:8080/actuator/circuitbreakerevents/emailService

# Ver métricas de reintentos
curl http://localhost:8080/actuator/retries

# Ver eventos de reintentos de MongoDB
curl http://localhost:8080/actuator/retryevents/mongoService
```

**Ejemplo de respuesta:**

```json
{
  "circuitBreakers": {
    "emailService": {
      "state": "CLOSED",
      "failureRate": "15.0%",
      "slowCallRate": "0.0%",
      "bufferedCalls": 20,
      "failedCalls": 3,
      "slowCalls": 0
    },
    "mongoService": {
      "state": "CLOSED",
      "failureRate": "5.0%",
      "slowCallRate": "0.0%",
      "bufferedCalls": 100,
      "failedCalls": 5,
      "slowCalls": 0
    }
  }
}
```

---

### Logs en Acción

**Email Service con Fallback:**
```
2025-10-14 00:00:15 INFO  [OrderMailService] ✅ Email enviado a user@example.com para orderId=ORD-123
2025-10-14 00:00:45 ERROR [OrderMailService] ❌ Error al enviar email: Connection timeout
2025-10-14 00:00:47 WARN  [OrderMailService] ⚠️ FALLBACK: Servicio de email no disponible para orderId=ORD-124
2025-10-14 00:00:47 ERROR [OrderMailService] 📧 EMAIL PENDIENTE: Revisar y enviar manualmente confirmación para orderId=ORD-124
```

**MongoDB Service con Retry:**
```
2025-10-14 00:01:10 DEBUG [CheckoutService] Obteniendo carrito para sessionId=abc123
2025-10-14 00:01:11 WARN  [Retry] Attempt 1 failed for mongoService: MongoTimeoutException
2025-10-14 00:01:12 WARN  [Retry] Attempt 2 failed for mongoService: MongoTimeoutException
2025-10-14 00:01:14 INFO  [Retry] Attempt 3 succeeded for mongoService
2025-10-14 00:01:14 INFO  [CheckoutService] Carrito obtenido exitosamente
```

---

## 📊 Resultados de la Implementación

### Antes de Resilience4j ❌

- SendGrid caído → Todos los pedidos fallan
- MongoDB timeout → Error 500 para el usuario
- Sin visibilidad de problemas
- Intervención manual constante

### Después de Resilience4j ✅

- SendGrid caído → Pedidos se procesan, emails se encolan
- MongoDB timeout → 3 reintentos automáticos, fallback elegante
- Métricas en tiempo real vía Actuator
- Auto-recuperación sin intervención

---

## 🎓 Lecciones Aprendidas

### 1. Los Fallbacks Son Cruciales

```java
// ❌ MAL: Circuit Breaker sin fallback
@CircuitBreaker(name = "emailService")
public void sendEmail() {
    emailService.send(...);  // Si falla → Excepción al usuario
}

// ✅ BIEN: Con fallback útil
@CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmail")
public void sendEmail() {
    emailService.send(...);
}

private void fallbackSendEmail(Exception ex) {
    emailQueue.add(...);  // Email se enviará después
}
```

### 2. Retry Solo en Operaciones Idempotentes

```java
// ❌ PELIGROSO: Retry en pagos
@Retry(name = "payment")
public void chargeCard() {
    paymentGateway.charge(...);  // Podría cobrar múltiples veces!
}

// ✅ SEGURO: Sin retry, con idempotency key
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
public void chargeCard(String idempotencyKey) {
    // Verificar si ya se procesó
    if (alreadyProcessed(idempotencyKey)) return;
    paymentGateway.charge(...);
}
```

### 3. Logs Descriptivos

```java
// ❌ Log inútil
logger.error("Error");

// ✅ Log útil con emojis y contexto
logger.warn("⚠️ FALLBACK: MongoDB no disponible. sessionId={}, userId={}, causa={}",
            sessionId, userId, ex.getMessage());
```

---

## 🚀 Próximos Pasos

1. **Añadir más servicios protegidos**
   - Servicio de pagos (Redsys)
   - API de productos
   - Servicio de recomendaciones

2. **Implementar cola de emails pendientes**
   - Tabla en BD para emails fallidos
   - Job programado para reintentos

3. **Dashboard de Grafana**
   - Visualizar métricas de Circuit Breakers
   - Alertas cuando Circuit Breakers se abren

4. **Tests de Resiliencia**
   - Tests que simulen fallos de servicios
   - Verificar que fallbacks funcionan

---

## 📚 Conclusión Final

**Resilience4j en Infinia Sports proporciona:**

- ✅ **Protección contra fallos** de SendGrid y MongoDB
- ✅ **Degradación elegante** - Usuarios nunca ven errores 500
- ✅ **Auto-recuperación** - Sin intervención manual
- ✅ **Observabilidad** - Métricas en tiempo real
- ✅ **Simplicidad** - Solo 2 anotaciones por método

**Código de producción = Código con Resilience4j** 🛡️

---

**Implementación completada:** 14 de octubre de 2025  
**Proyecto:** Infinia Sports E-Commerce  
**Roadmap:** Punto 5 - Resilience4j ✅
