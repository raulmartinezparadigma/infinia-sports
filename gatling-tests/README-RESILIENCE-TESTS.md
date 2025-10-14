# Pruebas de Resilience4j con Gatling

Este directorio contiene pruebas de carga específicas para validar las estrategias de resiliencia implementadas con Resilience4j.

## 🎯 Objetivo

Demostrar y validar que:
- **Circuit Breaker** protege la aplicación contra fallos en cascada
- **Retry** maneja fallos transitorios automáticamente
- **Fallbacks** proporcionan degradación elegante del servicio
- La aplicación permanece **estable bajo carga extrema**

## 📁 Archivos Principales

```
gatling-tests/
├── src/test/scala/com/infinia/sports/performance/
│   ├── Resilience4jStressSimulation.scala  ← Nueva simulación
│   ├── BasicSimulation.scala
│   ├── AnonymousUserSimulation.scala
│   ├── AuthenticatedUserSimulation.scala
│   └── FullLoadSimulation.scala
├── run-resilience-test.bat                  ← Script de ejecución rápida
└── README-RESILIENCE-TESTS.md               ← Este archivo
```

## 🚀 Inicio Rápido

### Paso 1: Iniciar el Backend

```bash
cd backend
mvn spring-boot:run
```

### Paso 2: Ejecutar las Pruebas

**Opción A - Script Automatizado (Recomendado):**

```bash
cd gatling-tests
run-resilience-test.bat
```

Este script:
- ✅ Verifica que el backend esté ejecutándose
- ✅ Abre el dashboard de monitoreo en el navegador
- ✅ Ejecuta la simulación
- ✅ Abre el reporte HTML automáticamente

**Opción B - Maven Directo:**

```bash
cd gatling-tests
mvn gatling:test -Dgatling.simulationClass=com.infinia.sports.performance.Resilience4jStressSimulation
```

## 📊 Qué Observar Durante la Prueba

### 1. Logs del Backend (Terminal)

Busca estos mensajes clave:

```log
✅ NORMAL: Carrito recuperado correctamente
⚠️ RETRY: Retry attempt 1/3 for method getCart
❌ FALLBACK: MongoDB no disponible, devolviendo carrito vacío
🔴 Circuit Breaker OPEN: Protegiendo sistema
🟡 Circuit Breaker HALF_OPEN: Intentando recuperación
🟢 Circuit Breaker CLOSED: Sistema recuperado
```

### 2. Dashboard de Actuator (Navegador)

El script abre automáticamente:
- http://localhost:8080/actuator/circuitbreakers

Actualiza la página periódicamente para ver cambios de estado:

| Estado | Color | Significado |
|--------|-------|-------------|
| **CLOSED** | 🟢 Verde | Todo funciona correctamente |
| **OPEN** | 🔴 Rojo | Protección activada, usando fallback |
| **HALF_OPEN** | 🟡 Amarillo | Probando recuperación |

### 3. Reporte de Gatling (HTML)

Al finalizar, se genera un reporte en:
```
target/gatling/resilience4jstresssimulation-[timestamp]/index.html
```

**Métricas Clave:**

| Métrica | Valor Objetivo | Significado |
|---------|----------------|-------------|
| **Successful Requests** | ≥ 70% | Incluyendo fallbacks |
| **Failed Requests** | ≤ 30% | Solo fallos reales |
| **95th Percentile Response Time** | < 5000 ms | Tiempo aceptable |
| **Mean Response Time** | < 2000 ms | Promedio bajo |

## 🧪 Escenarios de la Simulación

La simulación ejecuta **3 escenarios simultáneos**:

### Escenario 1: Estrés en Circuit Breaker
- **Usuarios:** 20 concurrentes
- **Duración:** 30 segundos
- **Objetivo:** Provocar apertura del Circuit Breaker
- **Endpoint:** `GET /api/cart` (50 peticiones por usuario)

### Escenario 2: Carga Normal
- **Usuarios:** 10 graduales
- **Duración:** 30 segundos
- **Objetivo:** Simular tráfico real en background
- **Flujo:** Login → Add to Cart → View Cart

### Escenario 3: Ráfaga Súbita
- **Usuarios:** 30 instantáneos
- **Inicio:** Después de 20 segundos
- **Objetivo:** Probar comportamiento ante picos de tráfico
- **Peticiones:** 20 por usuario en ráfaga

## 🔬 Experimentos Sugeridos

### Experimento 1: Ver Circuit Breaker en Acción Real

1. Inicia el backend y la prueba normalmente
2. **Durante la prueba**, detén MongoDB:
   ```bash
   net stop MongoDB
   ```
3. Observa:
   - Logs del backend: Verás reintentos y luego fallback
   - Dashboard Actuator: Circuit Breaker cambiará a OPEN
   - Gatling: Response time se mantendrá bajo (fallback rápido)
4. Reinicia MongoDB:
   ```bash
   net start MongoDB
   ```
5. Observa la recuperación automática: OPEN → HALF_OPEN → CLOSED

### Experimento 2: Ajustar Umbrales

Modifica `backend/src/main/resources/application.properties`:

```properties
# Hacer que el CB sea más sensible (para pruebas)
resilience4j.circuitbreaker.instances.mongoService.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.mongoService.failureRateThreshold=30
resilience4j.circuitbreaker.instances.mongoService.waitDurationInOpenState=10000
```

Reinicia el backend y ejecuta la prueba de nuevo.

### Experimento 3: Comparar Con y Sin Resilience4j

**Sin Resilience4j:**
- Comenta las anotaciones `@CircuitBreaker` y `@Retry` en `CheckoutServiceImpl.java`
- Reinicia backend
- Ejecuta prueba
- Resultado esperado: Timeouts masivos, aplicación saturada

**Con Resilience4j:**
- Descomenta las anotaciones
- Reinicia backend
- Ejecuta prueba
- Resultado esperado: Fallbacks funcionando, aplicación estable

## 📈 Consultar Métricas en Vivo

Mientras la prueba corre, usa estos comandos (en otra terminal):

```bash
# Estado de Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers | jq

# Eventos de Circuit Breaker
curl http://localhost:8080/actuator/circuitbreakerevents | jq

# Estado de Retries
curl http://localhost:8080/actuator/retries | jq

# Eventos de Retry
curl http://localhost:8080/actuator/retryevents/mongoService | jq

# Health Check General
curl http://localhost:8080/actuator/health | jq
```

## 🐛 Solución de Problemas

### Error: Backend no responde

```bash
# Verificar que el backend esté ejecutándose
curl http://localhost:8080/actuator/health
```

Si no responde:
```bash
cd backend
mvn spring-boot:run
```

### Error: No se encuentra la simulación

```bash
# Verificar que el archivo existe
ls src/test/scala/com/infinia/sports/performance/Resilience4jStressSimulation.scala
```

Si no existe, cópialo del repositorio.

### Error: Usuario de prueba no válido

Verifica que exista el usuario en `gatling-tests/src/test/resources/users.csv`:

```csv
username,password
drrivera,Daniel2008
```

### Error: MongoDB no disponible

Si MongoDB no está instalado/ejecutándose, puedes:
1. **Instalar MongoDB** (recomendado para pruebas completas)
2. **Ejecutar sin MongoDB**: El Circuit Breaker se abrirá inmediatamente y usará fallbacks

## 📚 Documentación Relacionada

- [**Guía Completa de Resilience4j**](../docs/resilience4j-guia-completa.md) - Conceptos y teoría
- [**Guía de Pruebas con Gatling**](../docs/resilience4j-pruebas-gatling.md) - Interpretación de resultados
- [**Spring Boot Actuator**](../docs/spring-boot-actuator.md) - Monitoreo y métricas

## ✅ Checklist de Validación

Después de ejecutar las pruebas, verifica:

- [ ] La simulación completó sin errores críticos
- [ ] El reporte HTML muestra ≥ 70% de peticiones exitosas
- [ ] Los logs del backend muestran reintentos y fallbacks
- [ ] El Circuit Breaker cambió de estado (CLOSED → OPEN → CLOSED)
- [ ] Las métricas de Actuator se actualizaron correctamente
- [ ] El tiempo de respuesta P95 se mantuvo < 5000ms

## 🎓 Conclusión

Si todos los checks pasan, has validado que:
- ✅ Resilience4j está correctamente configurado
- ✅ El sistema es resiliente bajo carga
- ✅ Los fallos se manejan con degradación elegante
- ✅ La aplicación está lista para producción

---

**¿Necesitas ayuda?** Revisa la [documentación completa](../docs/resilience4j-pruebas-gatling.md) o los logs del backend.
