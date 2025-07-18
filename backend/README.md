# Backend - Infinia Sports

Este módulo contiene la API RESTful para la aplicación Infinia Sports. Está construido con Java 17, Spring Boot y Maven.

## Arquitectura

El backend sigue una arquitectura limpia y desacoplada de 3 capas:

1.  **Capa de Controladores (`controller`)**: Expone los endpoints REST. Es responsable de gestionar las peticiones HTTP, validar la entrada básica y delegar la lógica de negocio a la capa de servicio. No contiene lógica de negocio compleja.
2.  **Capa de Servicios (`service`)**: Contiene la lógica de negocio principal de la aplicación. Orquesta las operaciones, interactúa con la capa de persistencia y realiza los cálculos o transformaciones de datos necesarios.
3.  **Capa de Persistencia (`repository` y `entity`)**: Gestiona la interacción con la base de datos (PostgreSQL y H2). Utiliza Spring Data JPA para definir los repositorios y MapStruct para el mapeo entre entidades y DTOs.

La premisa arquitectónica fundamental es que **los controladores nunca deben acceder directamente a los repositorios**.

## Tecnologías Clave

-   **Java 17**: Versión del JDK.
-   **Spring Boot**: Framework principal para la creación de la aplicación.
-   **Spring Security**: Para la gestión de autenticación y autorización basada en JWT.
-   **Spring Data JPA**: Para la persistencia de datos.
-   **PostgreSQL**: Base de datos para el entorno de producción y desarrollo (`dev`).
-   **H2 Database**: Base de datos en memoria para el perfil de pruebas (`test`).
-   **Maven**: Herramienta de construcción y gestión de dependencias.
-   **OpenAPI 3 (Swagger)**: Para la documentación automática de la API.
-   **Testcontainers**: Para pruebas de integración con una base de datos PostgreSQL real.

## Perfiles de Spring

El backend está configurado para funcionar con diferentes perfiles de Spring:

-   **`dev` (por defecto)**: Utiliza la base de datos PostgreSQL. Diseñado para el desarrollo local.
-   **`test`**: Utiliza la base de datos H2 en memoria. Se activa automáticamente durante la ejecución de las pruebas E2E para proporcionar un entorno limpio y aislado.

## Cómo Ejecutar el Backend

Para levantar el backend en modo de desarrollo, ejecuta el siguiente comando desde el directorio `backend`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

La aplicación se iniciará en `http://localhost:8080`.

### Documentación de la API

Una vez que la aplicación está en ejecución, puedes acceder a la interfaz de Swagger UI para explorar y probar los endpoints de la API en:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
