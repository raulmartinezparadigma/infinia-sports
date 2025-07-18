# Infinia Sports

_E-commerce de productos deportivos_

## Descripción General

Infinia Sports es una aplicación de comercio electrónico full-stack diseñada para la venta de productos deportivos. El proyecto está estructurado como un monorepo que contiene tres módulos principales:

-   **`backend`**: Una API RESTful construida con Java 17, Spring Boot y Maven. Sigue una arquitectura de 3 capas (Controlador, Servicio, Persistencia) y gestiona toda la lógica de negocio, autenticación, productos, pedidos y pagos.
-   **`frontend`**: Una Single Page Application (SPA) desarrollada con React.js que consume la API del backend para ofrecer una experiencia de usuario interactiva y moderna.
-   **`playwright-tests`**: Un módulo dedicado para las pruebas funcionales de extremo a extremo (E2E) utilizando Playwright con Java. Estas pruebas simulan interacciones reales de los usuarios en un entorno de navegador.

## Funcionalidades Clave

-   **Gestión de Productos**: CRUD completo de productos con persistencia en PostgreSQL.
-   **Autenticación y Seguridad**: Sistema de autenticación basado en JWT con roles de usuario (cliente y administrador) y refresco de token automático para mantener la sesión del usuario activa.
-   **Carrito de Compras**: Gestión completa del carrito de compras, sincronizado entre frontend y backend.
-   **Proceso de Checkout**: Flujo de pago integrado con cálculo de subtotales, gastos de envío e impuestos.
-   **Historial de Pedidos**: Los usuarios pueden consultar el historial de sus pedidos y ver los detalles de cada uno.
-   **Notificaciones por Email**: Envío de correos de confirmación de pedido utilizando SendGrid.

## Tecnologías Principales

-   **Backend**: Java 17, Spring Boot, Spring Security, Spring Data JPA, MapStruct.
-   **Frontend**: React.js, Axios, Material-UI (MUI).
-   **Bases de Datos**: PostgreSQL para datos relacionales (productos, usuarios) y H2 para el entorno de pruebas.
-   **Pruebas**: JUnit 5, Mockito (unitarias), Testcontainers (integración) y Playwright (E2E).
-   **Documentación API**: OpenAPI 3 (integrado con Swagger UI).
-   **Build Tool**: Maven.

## Puesta en Marcha y Ejecución

Sigue estas instrucciones para levantar el entorno de desarrollo y ejecutar las pruebas.

### Requisitos Previos

-   Java 17
-   Maven 3.8+
-   Node.js 18+
-   npm 9+

### 1. Ejecutar Backend y Frontend (Desarrollo)

Para el desarrollo diario, levanta el backend y el frontend por separado.

**Arrancar el Backend:**

```bash
# Navega al directorio del backend
cd backend

# Ejecuta la aplicación con el perfil 'dev'
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

El backend estará disponible en `http://localhost:8080` y la UI de Swagger en `http://localhost:8080/swagger-ui.html`.

**Arrancar el Frontend:**

```bash
# En una nueva terminal, navega al directorio del frontend
cd frontend

# Instala las dependencias y arranca el servidor de desarrollo
npm install
npm start
```

El frontend estará disponible en `http://localhost:3000`.

### 2. Ejecutar Pruebas End-to-End (E2E)

Se ha configurado un perfil de Maven (`e2e-test`) que orquesta todo el ciclo de vida de las pruebas E2E: compila, inicia el backend (con una base de datos H2 en memoria), inicia el frontend, ejecuta las pruebas de Playwright y detiene los servidores.

Para ejecutar la suite completa de pruebas E2E, ejecuta el siguiente comando desde la raíz del proyecto:

```bash
mvn verify -P e2e-test
```

Este comando se encarga de todo el proceso de forma automática. Los informes de las pruebas se encontrarán en el directorio `playwright-tests/target/surefire-reports/`.

## Configuración de SendGrid (Opcional)

Para que el envío de correos de confirmación funcione, necesitas una clave de API de SendGrid.

**Por seguridad, la clave API nunca debe incluirse en archivos versionados.** Debes exportarla como una variable de entorno antes de arrancar el backend:

```bash
# Ejemplo para Linux/macOS/Git Bash
export SENDGRID_API_KEY='TU_CLAVE_DE_SENDGRID'

# Ejemplo para PowerShell
$env:SENDGRID_API_KEY="TU_CLAVE_DE_SENDGRID"
```

## Convenciones y Arquitectura

-   **Idioma**: Nombres de clases, métodos y variables en **inglés**. Comentarios en **español**.
-   **Arquitectura Backend**: 3 capas (Controller, Service, Persistence). Ningún controlador accede directamente a un repositorio.
-   **Estilo de Código**: Se evitan los imports con `*` en Java y se mantiene la homogeneidad en el formato.
