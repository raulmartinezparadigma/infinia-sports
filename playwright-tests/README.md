# Pruebas End-to-End (E2E) con Playwright

Este módulo contiene la suite de pruebas funcionales de extremo a extremo (E2E) para la aplicación Infinia Sports. Utiliza Playwright con Java para simular interacciones de usuario reales en un entorno de navegador controlado.

## Objetivo

El propósito de estas pruebas es validar los flujos de usuario críticos de la aplicación de forma integrada, asegurando que el frontend y el backend funcionan correctamente juntos. Algunos de los flujos cubiertos son:

-   Autenticación de usuarios (Login/Logout).
-   Navegación por el catálogo de productos.
-   Gestión del carrito de compras (añadir, modificar y eliminar productos).
-   Proceso de checkout completo.
-   Consulta del historial de pedidos.

## Arquitectura y Entorno de Pruebas

Para garantizar pruebas fiables y aisladas, se ha configurado un entorno de pruebas autocontenido que se levanta y se destruye automáticamente:

-   **Backend**: Se inicia una instancia del servidor Spring Boot utilizando un perfil de Spring `test`.
-   **Base de Datos**: El perfil `test` utiliza una base de datos **H2 en memoria**, que se inicializa con datos de prueba (ej. usuario `testinfinia`/`123456`) antes de cada ejecución.
-   **Frontend**: El servidor de desarrollo de React se levanta en el puerto `3000`.
-   **Orquestación**: Todo el ciclo de vida (iniciar servidores, ejecutar pruebas, detener servidores) es gestionado por Maven a través del perfil `e2e-test`.

## Estrategia de Selectores

Para crear pruebas robustas y resistentes a cambios en la UI, la estrategia de selección de elementos es una prioridad. La convención principal es:

-   **Utilizar `data-testid`**: Los elementos interactivos clave en los componentes de React están instrumentados con un atributo `data-testid`.
-   **Usar `page.getByTestId()`**: Las pruebas de Playwright utilizan el método `getByTestId()` para localizar estos elementos de forma unívoca, desacoplando las pruebas de la estructura del DOM, los estilos CSS o el texto de la UI.

```java
// Ejemplo de selector robusto en una prueba
page.getByTestId("user-menu-button").click();
expect(page.getByTestId("logout-button")).toBeVisible();
```

## Cómo Ejecutar las Pruebas

Para lanzar la suite completa de pruebas E2E, ejecuta el siguiente comando desde el **directorio raíz del proyecto**:

```bash
mvn verify -P e2e-test
```

Este único comando se encarga de:
1.  Compilar todos los módulos.
2.  Iniciar el backend y el frontend en sus configuraciones de prueba.
3.  Ejecutar todas las pruebas de Playwright.
4.  Detener los servidores y limpiar los recursos.

Los informes de las pruebas se pueden encontrar en el directorio `playwright-tests/target/failsafe-reports/`.
