# Diagrama de Flujo: Pruebas de Resilience4j

Este documento proporciona una representación visual del flujo completo de pruebas y comportamiento de Resilience4j.

---

## 🎯 Arquitectura del Sistema de Pruebas

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRUEBAS DE RESILIENCE4J                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│                 │         │                 │         │                 │
│    GATLING      │────────▶│   INFINIA       │────────▶│    MONGODB      │
│   (Cliente)     │  HTTP   │   BACKEND       │  Query  │   (Database)    │
│                 │         │                 │         │                 │
└─────────────────┘         └─────────────────┘         └─────────────────┘
        │                           │                           │
        │                           │                           │
        │                    ┌──────▼──────┐                   │
        │                    │             │                   │
        │                    │ ACTUATOR    │                   │
        │                    │ (Métricas)  │                   │
        │                    │             │                   │
        │                    └──────┬──────┘                   │
        │                           │                           │
        ▼                           ▼                           ▼
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  Reporte HTML   │         │   Monitor       │         │   Logs &        │
│  - Métricas     │         │   - Dashboard   │         │   Eventos       │
│  - Gráficas     │         │   - Estados     │         │                 │
└─────────────────┘         └─────────────────┘         └─────────────────┘
```

---

## 🔄 Flujo de Ejecución de Pruebas

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. INICIO                                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ run-resilience-test.bat
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. VERIFICACIÓN DE DEPENDENCIAS                                │
│    ✓ Backend ejecutándose (puerto 8080)                        │
│    ✓ MongoDB disponible (puerto 27017)                         │
│    ✓ Usuario de prueba existente                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. APERTURA DE DASHBOARDS                                      │
│    → http://localhost:8080/actuator/circuitbreakers            │
│    → http://localhost:8080/actuator/health                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. EJECUCIÓN DE GATLING                                        │
│    ┌─────────────────────────────────────────────────┐        │
│    │  Escenario 1: Estrés (20 usuarios, 30s)        │        │
│    │  Escenario 2: Normal (10 usuarios, 30s)        │        │
│    │  Escenario 3: Ráfaga (30 usuarios, burst)      │        │
│    └─────────────────────────────────────────────────┘        │
│                                                                 │
│    Durante 60 segundos:                                        │
│    - Envía peticiones HTTP a /api/cart                        │
│    - Mide tiempos de respuesta                                │
│    - Cuenta éxitos y fallos                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. GENERACIÓN DE REPORTE                                       │
│    → target/gatling/resilience4jstresssimulation-[timestamp]/ │
│    → index.html (se abre automáticamente)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 6. ANÁLISIS DE RESULTADOS                                      │
│    ✓ Revisar métricas en HTML                                 │
│    ✓ Consultar eventos en Actuator                            │
│    ✓ Analizar logs del backend                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛡️ Flujo de Resilience4j: Petición Normal vs Con Fallos

### Caso 1: MongoDB Funciona Correctamente (CLOSED)

```
Usuario hace petición GET /api/cart
        │
        ▼
┌───────────────────────┐
│  CartController       │
│  @GetMapping("/cart") │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  CheckoutService      │
│  getCart()            │
│  @CircuitBreaker      │
│  @Retry               │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  CartRepository       │
│  findByUserId()       │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  MongoDB              │
│  ✅ Query exitosa     │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  Response 200 OK      │
│  { cart: {...} }      │
└───────────────────────┘

Estado CB: CLOSED
Tiempo: ~200ms
```

---

### Caso 2: MongoDB Lento → Retry Exitoso (CLOSED)

```
Usuario hace petición GET /api/cart
        │
        ▼
┌───────────────────────┐
│  CartController       │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  CheckoutService      │
│  @Retry (intento 1)   │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  MongoDB              │
│  ⏰ Timeout (1s)      │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  @Retry (intento 2)   │
│  Espera 1s            │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  MongoDB              │
│  ✅ Query exitosa     │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  Response 200 OK      │
│  { cart: {...} }      │
└───────────────────────┘

Estado CB: CLOSED
Tiempo: ~2200ms (1s timeout + 1s espera + 200ms query)
Log: "Retry attempt 1/3 for method getCart"
```

---

### Caso 3: MongoDB Caído → Fallback (OPEN)

```
Usuario hace petición GET /api/cart
        │
        ▼
┌───────────────────────┐
│  CartController       │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  CheckoutService      │
│  @CircuitBreaker      │
│  Estado: OPEN 🔴      │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  ⚡ Circuit Breaker   │
│  CallNotPermitted     │
│  Llamada bloqueada    │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  fallbackGetCart()    │
│  return carrito vacío │
└───────────────────────┘
        │
        ▼
┌───────────────────────┐
│  Response 200 OK      │
│  { cart: {            │
│    items: [],         │
│    total: 0           │
│  }}                   │
└───────────────────────┘

Estado CB: OPEN
Tiempo: ~50ms (fallback instantáneo)
Log: "⚠️ FALLBACK: MongoDB no disponible"
Usuario: ✅ No ve error 500, ve carrito vacío
```

---

## 📊 Transiciones de Estado del Circuit Breaker

```
┌─────────────────────────────────────────────────────────────────┐
│                  CICLO DE VIDA DEL CIRCUIT BREAKER              │
└─────────────────────────────────────────────────────────────────┘

                    ┌──────────────────┐
                    │                  │
          ┌─────────│     CLOSED       │◀────────────┐
          │         │   (🟢 Normal)    │             │
          │         │                  │             │
          │         └──────────────────┘             │
          │                  │                       │
          │                  │ Failure Rate > 50%    │
          │                  │ (10 llamadas mín.)    │
          │                  │                       │
          │                  ▼                       │
          │         ┌──────────────────┐            │
          │         │                  │            │
          │         │      OPEN        │            │
          │         │  (🔴 Protegido)  │            │
          │         │                  │            │
          │         │  Fallback activo │            │
          │         │                  │            │
          │         └──────────────────┘            │
          │                  │                       │
          │                  │ Después de 60s       │
          │                  │ (waitDuration)        │
          │                  │                       │
          │                  ▼                       │
          │         ┌──────────────────┐            │
          │         │                  │            │
          │         │   HALF_OPEN      │            │
          │         │  (🟡 Probando)   │            │
          │         │                  │            │
          │         │  Permite 10      │            │
          │         │  llamadas prueba │            │
          │         │                  │            │
          │         └──────────────────┘            │
          │                  │                       │
          │          ┌───────┴───────┐              │
          │          │               │              │
          │          ▼               ▼              │
          │  ┌──────────────┐ ┌──────────────┐    │
          │  │  Éxito > 50% │ │ Fallo > 50%  │    │
          │  └──────────────┘ └──────────────┘    │
          │          │               │              │
          │          │               ▼              │
          └──────────┘      Vuelve a OPEN ─────────┘


┌─────────────────────────────────────────────────────────────────┐
│ EJEMPLO DE TRANSICIÓN DURANTE LA PRUEBA                        │
└─────────────────────────────────────────────────────────────────┘

T=0s    │ CLOSED    │ Todo funciona, failure rate = 5%
T=10s   │ CLOSED    │ Gatling aumenta carga
T=15s   │ CLOSED    │ MongoDB empieza a fallar, failure rate = 35%
T=20s   │ OPEN      │ Failure rate = 55%, CB se abre
T=20s   │ OPEN      │ Fallbacks activados, response time baja a 50ms
T=40s   │ OPEN      │ Sigue en OPEN, fallbacks funcionando
T=80s   │ HALF_OPEN │ Transcurrieron 60s, probando recuperación
T=82s   │ CLOSED    │ MongoDB recuperado, éxito > 50%
T=90s   │ CLOSED    │ Sistema normal, failure rate = 8%
```

---

## 📈 Métricas Observables Durante la Prueba

### Vista del Dashboard de Actuator

```
┌─────────────────────────────────────────────────────────────────┐
│ Circuit Breaker: mongoService                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Estado Actual: OPEN 🔴                                         │
│                                                                  │
│  Métricas (últimas 100 llamadas):                              │
│  ┌──────────────────────────────────────────────────┐          │
│  │ Llamadas exitosas:    ████████░░░░░░░░░░  40     │          │
│  │ Llamadas fallidas:    ████████████████░░  60     │          │
│  │ Llamadas no permitidas: ██████████████    200    │          │
│  │                                                   │          │
│  │ Failure Rate:         60% ⚠️                     │          │
│  │ Slow Call Rate:       5%                         │          │
│  └──────────────────────────────────────────────────┘          │
│                                                                  │
│  Última transición:                                             │
│  CLOSED → OPEN (hace 12 segundos)                              │
│  Causa: Failure rate threshold exceeded (60% > 50%)            │
│                                                                  │
│  Próxima transición a HALF_OPEN:                               │
│  En 48 segundos                                                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Vista del Reporte de Gatling

```
┌─────────────────────────────────────────────────────────────────┐
│ Resilience4j Stress Simulation - Results                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Requests:                 3,000 total                          │
│  Successful:               2,400 (80%) ✅                       │
│  Failed:                     600 (20%)                          │
│                                                                  │
│  Response Times:                                                │
│  ┌──────────────────────────────────────────────────┐          │
│  │ Min:            42 ms                             │          │
│  │ Mean:          850 ms                             │          │
│  │ Max:         4,823 ms                             │          │
│  │ 50th %:        520 ms  ████████████░░░░░░░       │          │
│  │ 75th %:      1,200 ms  ██████████████████░░░     │          │
│  │ 95th %:      3,500 ms  ████████████████████████  │          │
│  │ 99th %:      4,500 ms  ██████████████████████████│          │
│  └──────────────────────────────────────────────────┘          │
│                                                                  │
│  Requests per Second:                                           │
│  ┌──────────────────────────────────────────────────┐          │
│  │   80│        ╱╲                                   │          │
│  │   60│       ╱  ╲      ╱╲                         │          │
│  │   40│      ╱    ╲    ╱  ╲                        │          │
│  │   20│   ╱╱      ╲╲╱╱    ╲╲╲____                  │          │
│  │    0│───────────────────────────────▶ Time       │          │
│  │     0s  10s  20s  30s  40s  50s  60s             │          │
│  └──────────────────────────────────────────────────┘          │
│                                                                  │
│  Conclusión: ✅ PASSED                                          │
│  - 80% de éxito (objetivo: ≥70%)                               │
│  - P95 < 5s (objetivo: <5000ms)                                │
│  - Sistema resiliente bajo carga                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔍 Comparativa: Con vs Sin Resilience4j

### ❌ SIN Resilience4j

```
MongoDB falla
    │
    ▼
Peticiones se acumulan esperando timeout (30s)
    │
    ▼
Pool de threads agotado
    │
    ▼
Aplicación deja de responder
    │
    ▼
💥 TODA LA APLICACIÓN CAÍDA


Métricas de Gatling:
- Timeout rate: 95%
- Response time P95: 30,000 ms
- Failed requests: 95%
- Resultado: ❌ FALLO TOTAL
```

### ✅ CON Resilience4j

```
MongoDB falla
    │
    ▼
Retry intenta 3 veces (1s, 2s, 4s)
    │
    ▼
Circuit Breaker detecta 50% fallos
    │
    ▼
CB se abre, activa fallback
    │
    ▼
Usuarios reciben carrito vacío (50ms)
    │
    ▼
✅ APLICACIÓN ESTABLE


Métricas de Gatling:
- Success rate: 80% (con fallback)
- Response time P95: 3,500 ms
- Failed requests: 20%
- Resultado: ✅ SISTEMA RESILIENTE
```

---

## 📚 Leyenda de Símbolos

| Símbolo | Significado |
|---------|-------------|
| 🟢 | Estado normal, todo OK |
| 🟡 | Estado intermedio, probando |
| 🔴 | Estado de protección, usando fallback |
| ✅ | Operación exitosa |
| ⚠️ | Advertencia, requiere atención |
| ❌ | Fallo, error |
| ⏰ | Timeout |
| ⚡ | Acción rápida/instantánea |
| 💥 | Fallo catastrófico |
| 📊 | Métricas |
| 🔄 | Retry/Reintento |
| 🛡️ | Protección activa |

---

## 🎯 Puntos Clave para Recordar

1. **Circuit Breaker = Interruptor Automático**
   - Se abre cuando detecta muchos fallos
   - Protege el sistema de sobrecarga
   - Se recupera automáticamente

2. **Retry = Persistencia Inteligente**
   - Reintenta operaciones fallidas
   - Usa espera exponencial
   - Límite de intentos configurable

3. **Fallback = Plan B**
   - Se ejecuta cuando todo falla
   - Proporciona respuesta alternativa
   - Mantiene experiencia de usuario

4. **La Prueba Valida Todo Esto**
   - Gatling genera la carga
   - Resilience4j protege el sistema
   - Actuator muestra las métricas
   - El reporte HTML valida el éxito

---

**📌 Siguiente paso:** Ejecuta `run-resilience-test.bat` y observa este flujo en acción.
