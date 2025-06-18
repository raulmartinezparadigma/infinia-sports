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
