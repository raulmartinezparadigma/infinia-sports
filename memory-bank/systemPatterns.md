# Patrones del Sistema - Infinia Sports

## Arquitectura del Sistema

### Arquitectura General
El sistema sigue una arquitectura de aplicación web moderna con separación clara entre frontend y backend:

```mermaid
flowchart TD
    Cliente[Cliente Web] <--> API[API REST]
    API <--> Servicios[Capa de Servicios]
    Servicios <--> Persistencia[Capa de Persistencia]
    Persistencia <--> PSQL[(PostgreSQL)]
    Persistencia <--> MongoDB[(MongoDB)]
```

### Backend (Java 17)
El backend implementa una arquitectura de 3 capas bien definida:

```mermaid
flowchart TD
    Controller[Controller] --> Service[Service]
    Service --> Persistence[Persistence]
    Persistence --> DB[(Bases de Datos)]
```

1. **Capa de Controller**: Gestiona las peticiones HTTP y respuestas, implementa endpoints REST.
2. **Capa de Service**: Contiene la lógica de negocio y validaciones.
3. **Capa de Persistence**: Gestiona el acceso a datos y operaciones con las bases de datos.

### Frontend (React.js)
El frontend sigue una arquitectura de componentes con separación de responsabilidades:

```mermaid
flowchart TD
    App[App] --> Pages[Pages]
    Pages --> Components[Components]
    Components --> Common[Common Components]
    Pages --> Services[API Services]
    Services --> API[Backend API]
```

## Decisiones Técnicas Clave

### Premisa Arquitectónica Clave: Controllers deben usar Services
**Regla**: Ningún controlador debe acceder directamente a los repositorios. Siempre debe existir una clase de servicio (Service) entre el controlador y el repositorio. Esta es una regla estricta que aplica a todos los controladores existentes y futuros para asegurar una correcta separación de responsabilidades y mantener la lógica de negocio aislada en la capa de servicio.

### Persistencia Dual
- **PostgreSQL**: Utilizado para datos estructurados (Productos) que requieren consultas complejas y relaciones.
- **MongoDB**: Utilizado para datos con estructura variable y compleja (Pedidos) que se benefician del formato JSON.

### Arquitectura de 3 Capas
Implementación estricta de la separación de responsabilidades:
- Controllers solo gestionan peticiones/respuestas HTTP
- Services contienen toda la lógica de negocio
- Persistence se encarga exclusivamente del acceso a datos

### Documentación API con OpenAPI
Uso de OpenAPI 3.0.x para documentar todos los endpoints, facilitando:
- Pruebas de API
- Generación de clientes
- Documentación interactiva

## Patrones de Diseño en Uso

### Patrón Repository
Implementado en la capa de persistencia para abstraer el acceso a datos.

### Patrón Service
Encapsula la lógica de negocio y orquesta operaciones complejas.

### Patrón DTO (Data Transfer Object)
Utilizado para transferir datos entre capas y hacia/desde el cliente.

### Patrón Factory (potencial)
Para la creación de objetos complejos como pedidos.

## Patrones de Seguridad

### Autenticación y Autorización Basada en Roles JWT
Se ha refactorizado `JwtAuthenticationFilter` para que el tipo de `UserDetailsService` (para administradores o para usuarios normales) se seleccione en función de los roles (`ROLE_ADMIN`) presentes en el token JWT, en lugar de basarse en la ruta de la petición. Este enfoque es más seguro y robusto, especialmente para endpoints que pueden ser accedidos por diferentes tipos de usuario (ej. `/api/orders`).

## Relaciones entre Componentes

### Módulo de Productos
```mermaid
flowchart TD
    ProductoController --> ProductoService
    ProductoService --> ProductoPersistence
    ProductoPersistence --> PostgreSQL[(PostgreSQL)]
```

### Módulo de Checkout
```mermaid
flowchart TD
    CheckoutController --> CheckoutService
    CheckoutService --> CheckoutPersistence
    CheckoutService --> ProductoService
    CheckoutPersistence --> MongoDB[(MongoDB)]
```

### Módulo de Pagos
```mermaid
flowchart TD
    PagoController --> PagoService
    PagoService --> PagoPersistence
    PagoService --> CheckoutService
    PagoService --> EmailSender[Email Sender]
    PagoPersistence --> MongoDB[(MongoDB)]
```

## Consideraciones de Arquitectura
- Separación clara de responsabilidades
- Minimización de dependencias entre módulos
- Interfaces bien definidas entre capas
- Manejo consistente de errores
- Validaciones en la capa de servicio
