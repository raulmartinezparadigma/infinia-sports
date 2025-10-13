# Spring Boot Actuator - Monitorización en Producción

**Fecha de implementación:** 13 de octubre de 2025  
**Estado:** ✅ Implementado

## 📋 ¿Qué es Spring Boot Actuator?

Spring Boot Actuator proporciona endpoints listos para producción que permiten monitorizar y gestionar la aplicación. Ofrece información sobre:

- Estado de salud de la aplicación
- Métricas de rendimiento
- Información de la aplicación
- Configuración del entorno
- Logs en tiempo real

## 🚀 Endpoints Disponibles

Base URL: `http://localhost:8080/actuator`

### 1. Health Check - `/actuator/health`

**Descripción:** Muestra el estado de salud de la aplicación y sus componentes

**Ejemplo de respuesta:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 123456789012,
        "threshold": 10485760,
        "exists": true
      }
    },
    "mongo": {
      "status": "UP",
      "details": {
        "version": "6.0.0"
      }
    }
  }
}
```

**Uso en producción:**
```bash
# Health check simple
curl http://localhost:8080/actuator/health

# Health check con detalles (requiere autorización)
curl -H "Authorization: Bearer <token>" http://localhost:8080/actuator/health
```

### 2. Info - `/actuator/info`

**Descripción:** Información sobre la aplicación

**Ejemplo de respuesta:**
```json
{
  "app": {
    "name": "Infinia Sports Backend",
    "description": "E-commerce de productos deportivos",
    "version": "0.0.1-SNAPSHOT",
    "encoding": "UTF-8",
    "java": {
      "version": "17"
    }
  }
}
```

### 3. Metrics - `/actuator/metrics`

**Descripción:** Lista de todas las métricas disponibles

**Ejemplo de respuesta:**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "jvm.gc.pause",
    "system.cpu.usage",
    "http.server.requests",
    "hikaricp.connections.active",
    "mongodb.driver.commands"
  ]
}
```

**Métricas específicas:**

#### Uso de memoria JVM
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

```json
{
  "name": "jvm.memory.used",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 123456789
    }
  ],
  "availableTags": [
    {
      "tag": "area",
      "values": ["heap", "nonheap"]
    }
  ]
}
```

#### Requests HTTP
```bash
curl http://localhost:8080/actuator/metrics/http.server.requests
```

```json
{
  "name": "http.server.requests",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1234
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 45.678
    },
    {
      "statistic": "MAX",
      "value": 2.5
    }
  ]
}
```

#### Conexiones de base de datos
```bash
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

### 4. Environment - `/actuator/env`

**Descripción:** Propiedades de configuración y variables de entorno

**Ejemplo de uso:**
```bash
# Ver todas las propiedades
curl http://localhost:8080/actuator/env

# Ver una propiedad específica
curl http://localhost:8080/actuator/env/server.port
```

⚠️ **Seguridad:** Este endpoint puede exponer información sensible. Debe estar protegido en producción.

### 5. Loggers - `/actuator/loggers`

**Descripción:** Ver y modificar niveles de logging en tiempo real

**Ver nivel de un logger:**
```bash
curl http://localhost:8080/actuator/loggers/com.infinia.sports
```

**Cambiar nivel de logging (POST):**
```bash
curl -X POST \
  http://localhost:8080/actuator/loggers/com.infinia.sports \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'
```

### 6. HTTP Trace - `/actuator/httptrace`

**Descripción:** Historial de las últimas 100 peticiones HTTP

**Ejemplo de respuesta:**
```json
{
  "traces": [
    {
      "timestamp": "2025-10-13T15:30:00.000+00:00",
      "principal": null,
      "session": null,
      "request": {
        "method": "GET",
        "uri": "http://localhost:8080/api/products",
        "headers": {
          "accept": ["application/json"]
        }
      },
      "response": {
        "status": 200,
        "headers": {
          "Content-Type": ["application/json"]
        }
      },
      "timeTaken": 45
    }
  ]
}
```

### 7. Thread Dump - `/actuator/threaddump`

**Descripción:** Información sobre todos los threads de la aplicación

**Uso:** Útil para diagnosticar deadlocks o problemas de rendimiento

### 8. Heap Dump - `/actuator/heapdump`

**Descripción:** Descarga un heap dump de la JVM

**Uso:**
```bash
curl http://localhost:8080/actuator/heapdump -o heapdump.hprof
```

## 🔒 Configuración de Seguridad

### Para Desarrollo (Actual)

Todos los endpoints están expuestos sin autenticación para facilitar el desarrollo:

```properties
management.endpoints.web.exposure.include=health,info,metrics,env,loggers,httptrace,threaddump,heapdump
```

### Para Producción (Recomendado)

```properties
# Solo exponer endpoints seguros públicamente
management.endpoints.web.exposure.include=health,info,metrics

# Requerir autenticación para detalles de health
management.endpoint.health.show-details=when-authorized

# Endpoints sensibles solo en localhost
management.server.address=127.0.0.1
```

## 📊 Integración con Herramientas de Monitorización

### Prometheus (Métricas)

1. Añadir dependencia:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

2. Configurar endpoint:
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

3. Endpoint de Prometheus: `http://localhost:8080/actuator/prometheus`

### Grafana (Visualización)

1. Configurar Prometheus como datasource
2. Importar dashboard: Spring Boot 2.1 System Monitor (ID: 11378)
3. Visualizar métricas en tiempo real

### ELK Stack (Logs)

Los logs pueden ser enviados a Elasticsearch usando Logstash o Filebeat.

## 🎯 Casos de Uso Comunes

### 1. Health Check para Load Balancer

```bash
# Script de health check
#!/bin/bash
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
if [ "$HEALTH" == "UP" ]; then
  exit 0
else
  exit 1
fi
```

### 2. Monitorización de Memoria

```bash
# Alerta si la memoria heap supera el 80%
MEMORY_USED=$(curl -s http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:heap | jq '.measurements[0].value')
MEMORY_MAX=$(curl -s http://localhost:8080/actuator/metrics/jvm.memory.max?tag=area:heap | jq '.measurements[0].value')
PERCENTAGE=$(echo "scale=2; $MEMORY_USED / $MEMORY_MAX * 100" | bc)
if (( $(echo "$PERCENTAGE > 80" | bc -l) )); then
  echo "ALERT: Memory usage at ${PERCENTAGE}%"
fi
```

### 3. Debugging de Rendimiento

```bash
# Ver las peticiones más lentas
curl http://localhost:8080/actuator/metrics/http.server.requests | jq '.measurements[] | select(.statistic=="MAX")'
```

### 4. Cambiar Logging en Caliente

```bash
# Activar DEBUG para debugging
curl -X POST http://localhost:8080/actuator/loggers/com.infinia.sports.service \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'

# Volver a INFO cuando termine
curl -X POST http://localhost:8080/actuator/loggers/com.infinia.sports.service \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "INFO"}'
```

## 📈 Métricas Personalizadas (Futuro)

Ejemplo de cómo añadir métricas custom:

```java
@Component
public class OrderMetrics {
    private final Counter ordersCreated;
    private final Timer orderProcessingTime;
    
    public OrderMetrics(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders.created")
            .description("Total orders created")
            .tag("type", "ecommerce")
            .register(registry);
            
        this.orderProcessingTime = Timer.builder("orders.processing.time")
            .description("Time to process an order")
            .register(registry);
    }
    
    public void recordOrder() {
        ordersCreated.increment();
    }
    
    public void recordProcessingTime(Runnable task) {
        orderProcessingTime.record(task);
    }
}
```

## 🔍 Troubleshooting

### Endpoint no disponible

**Problema:** `404 Not Found` en `/actuator/health`

**Solución:**
1. Verificar que la dependencia está en el `pom.xml`
2. Verificar que el endpoint está en `management.endpoints.web.exposure.include`
3. Limpiar y recompilar: `mvn clean compile`

### Sin detalles en health

**Problema:** Solo muestra `{"status": "UP"}`

**Solución:**
```properties
management.endpoint.health.show-details=always
```

### Endpoints bloqueados por seguridad

**Problema:** `403 Forbidden`

**Solución:** Configurar Spring Security para permitir acceso a `/actuator/**`

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
            .antMatchers("/actuator/health", "/actuator/info").permitAll()
            .antMatchers("/actuator/**").hasRole("ADMIN")
            // ...
}
```

## 📚 Recursos

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Production-Ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints)

## ✅ Checklist de Implementación

- [x] Dependencia añadida al `pom.xml`
- [x] Configuración básica en `application.properties`
- [x] Endpoints expuestos y probados
- [x] Documentación creada
- [ ] Tests de endpoints de actuator (opcional)
- [ ] Configuración de seguridad para producción
- [ ] Integración con Prometheus/Grafana (opcional)
- [ ] Métricas personalizadas (futuro)

---

**Implementado por:** Cascade AI  
**Fecha:** 13 de octubre de 2025  
**Roadmap:** Punto 2 - Spring Boot Actuator
