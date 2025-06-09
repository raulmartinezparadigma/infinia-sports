# Infinia Sports

_E-commerce de productos deportivos_

## Estado del proyecto
- **Backend Java 17 (Spring Boot) en arquitectura de 3 capas**: Controller, Service, Persistence
- **Frontend**: React.js
- **Bases de datos**: PostgreSQL (productos) y MongoDB (pedidos)
- **Documentación API**: OpenAPI 3.0.x (Swagger UI)

## Módulos implementados
### 1. Módulo de Productos
- CRUD completo sobre entidad `Product` (id, type, description, price, size)
- Importación masiva de productos
- Validación de campos y tipos
- Persistencia en PostgreSQL
- Endpoints REST:
  - `GET /productos`
  - `GET /productos/{id}`
  - `POST /productos`
  - `PUT /productos/{id}`
  - `DELETE /productos/{id}`
  - `POST /productos/importar`

### 2. Módulo de Checkout
- Gestión de carrito y pedidos (estructura JSON compleja)
- Persistencia en MongoDB
- Endpoints implementados: `/cart`, `/cart/items`, `/checkout`, `/orders`
- Integración frontend-backend funcional (React + Spring Boot)
- PUT `/cart/items/{id}` requiere en el body `{ id, productId, quantity }` (ver troubleshooting)
- Error 400 resuelto: el backend valida que `productId` sea obligatorio en el body para actualizar cantidad

### 3. Módulo de Pagos
- Integración real con Bizum, Redsys (tarjeta) y Transferencia bancaria
- Tras cualquier pago, el usuario es dirigido a una pantalla de confirmación única, con mensaje contextual según el método utilizado
- El carrito se vacía y sincroniza tras cualquier pago para máxima coherencia frontend-backend
- El frontend utiliza React Router y state para pasar el método de pago a la pantalla de confirmación
- Envío de correo de confirmación tras pedido

## Tecnologías principales
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- MongoDB
- React.js (frontend)
- OpenAPI 3.0.x (Swagger)

## Cómo arrancar el backend
1. Asegúrate de tener **Java 17** y **Maven** instalados y configurados en tu PATH.
2. Desde la carpeta `backend`, ejecuta:
   ```bash
   mvn clean install
   java -jar target/sports-0.0.1-SNAPSHOT.jar
   ```
   El backend arrancará en [http://localhost:8080](http://localhost:8080)
3. Accede a la documentación Swagger UI en:
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Notas
- No existen scripts auxiliares en `backend` (Windows/Linux): el arranque se realiza solo con Maven y Java.
- Los nombres de clases, métodos y variables están en inglés; los comentarios en español.
- Arquitectura y convenciones alineadas con la planificación del proyecto.

## Próximos pasos
- Implementar módulo de Checkout y Pagos
- Desarrollar frontend React
- Pruebas de integración y despliegue
