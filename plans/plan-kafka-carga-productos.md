# Plan de integración de Kafka para carga de productos

## 1. Objetivo General
Desacoplar la carga de productos del DataInitializer usando una cola Kafka, permitiendo la carga masiva y asíncrona de productos desde un panel de administración en el futuro.

## 2. Alcance de esta fase
- Solo planificación y diseño, sin implementación de código aún.
- El plan se almacenará en la carpeta `plans/` y se inicializará el memory-bank.
- Separación estricta entre configuración de Kafka y lógica de negocio.

## 3. Pasos detallados

### 3.1. Análisis y diseño de arquitectura
- Identificar el flujo actual de carga de productos en `DataInitializer`.
- Diseñar el nuevo flujo:
  - Los productos a cargar se enviarán a un topic Kafka.
  - Un consumidor Kafka en el backend escuchará ese topic y persistirá los productos en la base de datos.
- El panel de administración (frontend) podrá enviar productos al topic Kafka mediante un endpoint REST (a implementar en el futuro).

---

## 4. Guía de instalación y configuración para infraestructura Kafka

### 4.1. Requisitos previos (Windows y Mac)
- **Docker Desktop** ([descargar](https://www.docker.com/products/docker-desktop/))
- **Git**
  - Windows: [descargar Git Bash](https://git-scm.com/download/win)
  - Mac: `brew install git`

### 4.2. Pasos para ambos sistemas

1. **Clona el repositorio**
   ```bash
   git clone <URL_DEL_REPO>
   cd infinia-sports
   ```

2. **Arranca Kafka y Zookeeper con Docker Compose**
   - Asegúrate de que Docker Desktop está abierto y en ejecución.
   - Ejecuta:
     ```bash
     docker-compose up -d kafka zookeeper
     ```
   - (Opcional) Para probar persistencia real, puedes arrancar también PostgreSQL:
     ```bash
     docker-compose up -d postgres
     ```

3. **Verifica que los contenedores están corriendo**
   ```bash
   docker ps
   ```

### 4.3. Notas específicas por sistema

#### Windows
- Se recomienda usar **Git Bash** o **PowerShell** para los comandos.
- Si tienes conflictos de puertos, asegúrate de que no hay otro PostgreSQL o Kafka local ejecutándose.
- Para limpiar todo y reiniciar:
  ```bash
  docker-compose down -v
  docker-compose up -d
  ```

#### Mac
- Si usas Apple Silicon (M1/M2), Docker Desktop lo soporta sin problemas.
- Si algún comando da error de permisos, usa `chmod +x <script>`.
- Puedes instalar Git y otras utilidades con Homebrew: `brew install git`

### 4.4. Resumen rápido de comandos

```bash
# 1. Clonar el repositorio
git clone <URL_DEL_REPO>
cd infinia-sports

# 2. Arrancar Kafka y Zookeeper
# (igual en Windows y Mac)
docker-compose up -d kafka zookeeper

# 3. (Opcional) Arrancar PostgreSQL para pruebas completas
docker-compose up -d postgres
```

---


### 3.2. Configuración de Kafka (backend)
- Crear una configuración dedicada de Kafka en un archivo separado (ej: `KafkaConfig.java`).
- Definir propiedades de productor y consumidor (bootstrap servers, serializadores, etc).
- No mezclar la configuración de Kafka con la lógica de negocio ni con otras configuraciones de Spring.

### 3.3. Productor Kafka para productos
- Crear un servicio productor que envíe mensajes de productos al topic correspondiente.
- Este servicio será invocado por el DataInitializer (en esta primera fase) y, en el futuro, por el endpoint del panel de administración.

### 3.4. Consumidor Kafka para productos
- Crear un listener/consumer que escuche el topic de productos y persista cada producto recibido en la base de datos PostgreSQL.
- Manejar errores de deserialización y persistencia (logging, reintentos si es necesario).

### 3.5. Refactorización de DataInitializer
- Modificar `DataInitializer` para que, en vez de guardar directamente los productos, los envíe a Kafka mediante el productor.

### 3.6. Preparación para integración frontend (futuro)
- Definir el endpoint REST que permitirá al panel de administración enviar productos masivamente (invocando el productor Kafka).
- No implementar aún la UI, solo dejar el diseño y el endpoint planeados.

### 3.7. Documentación y pruebas
- Documentar el flujo en OpenAPI y en el README.
- Planificar pruebas unitarias y de integración para el productor y consumidor Kafka.

### 3.8. Consideraciones de despliegue
- Añadir instrucciones para levantar Kafka en desarrollo (docker-compose, etc).
- Documentar variables de entorno/configuración necesarias.

## 4. Estructura de carpetas y archivos a crear/modificar

- `backend/src/main/java/com/infinia/sports/config/KafkaConfig.java` (configuración Kafka)
- `backend/src/main/java/com/infinia/sports/kafka/ProductProducer.java` (servicio productor)
- `backend/src/main/java/com/infinia/sports/kafka/ProductConsumer.java` (servicio consumidor)
- `backend/src/main/java/com/infinia/sports/config/DataInitializer.java` (refactor)
- `plans/plan-kafka-carga-productos.md` (este plan)
- `memory-bank/` (inicialización y actualización)

## 4.1. Persistencia robusta e idempotencia

- El DataInitializer asigna un UUID único a cada producto antes de enviarlo a Kafka. El campo `id` del ProductKafkaMessage siempre es un UUID válido como String.
- El consumidor (ProductConsumer) convierte el id de String a UUID y antes de guardar comprueba si ya existe en la base de datos (`existsById`). Si existe, no lo inserta de nuevo (control idempotente).
- Así se garantiza que los productos se almacenan de forma persistente en PostgreSQL y no se duplican aunque el flujo se repita.

## 5. Estructura del mensaje de producto para Kafka

### 5.1. Formato del mensaje
- Formato: JSON
- Clase Java: `ProductKafkaMessage` (en paquete `com.infinia.sports.kafka.dto`)

### 5.2. Campos mínimos requeridos
| Campo       | Tipo        | Descripción                                        |
|-------------|------------|----------------------------------------------------|
| id          | String/UUID | Identificador único del producto                   |
| type        | String      | Tipo de producto                                   |
| description | String      | Descripción del producto                           |
| price       | BigDecimal  | Precio                                             |
| size        | String      | Talla                                              |
| imageUrl    | String      | Nombre/URL de la imagen                            |

### 5.3. Ejemplo de mensaje JSON
```json
{
  "id": "b6b7e2d1-5c3f-4d7c-9b9a-1a2b3c4d5e6f",
  "type": "zapatillas",
  "description": "Nike Air Max 90",
  "price": 129.99,
  "size": "42",
  "imageUrl": "nike_air_max_90.jpg"
}
```

### 5.4. Clase Java sugerida
```java
public class ProductKafkaMessage {
    private String id;
    private String type;
    private String description;
    private BigDecimal price;
    private String size;
    private String imageUrl;
    // Getters, setters, constructor, toString
}
```

### 5.5. Validaciones
- Validar campos obligatorios antes de enviar.
- El consumidor debe validar el mensaje y loguear/rechazar productos malformados.
- Mantener compatibilidad con la entidad Product.

## 6. Futuras extensiones (no implementar ahora)
- Endpoint REST para carga masiva desde frontend.
- UI en el panel de administración para carga de productos.
- Gestión de errores avanzada y reintentos.

## 7. Próximos pasos
1. Guardar este plan en `plans/plan-kafka-carga-productos.md`.
2. Inicializar/actualizar el memory-bank con este plan y el estado del proyecto.
