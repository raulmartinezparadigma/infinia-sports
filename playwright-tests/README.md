# Playwright Tests para Infinia Sports

Este módulo contiene las pruebas end-to-end (E2E) para la aplicación Infinia Sports, utilizando Microsoft Playwright con Java.

## Configuración

### Estructura del Proyecto

- **Módulo Maven**: `playwright-tests`
- **Dependencias principales**: 
  - Microsoft Playwright
  - JUnit 5
  - Spring Boot (para integración con el backend)

### Arquitectura de Pruebas

El módulo está configurado para ejecutar pruebas E2E completas que involucran:

1. **Backend Spring Boot**: Se inicia automáticamente en el puerto 8080 con un perfil específico para pruebas
2. **Frontend React**: Se inicia automáticamente en el puerto 3000
3. **Playwright**: Controla un navegador para simular interacciones de usuario

### Clases Principales

- **BaseTest.java**: Clase base que configura Playwright, inicia el navegador y proporciona métodos comunes
- **LoginTest.java**: Pruebas de autenticación
- **ShoppingCartTest.java**: Pruebas del carrito de compras
- **OrderHistoryTest.java**: Pruebas de historial de pedidos

### Ciclo de Ejecución

El ciclo de vida de las pruebas está orquestado mediante plugins de Maven:

1. **Pre-integration-test**:
   - `spring-boot-maven-plugin`: Inicia el backend en modo test
   - `exec-maven-plugin`: Inicia el frontend React
   - `maven-antrun-plugin`: Espera a que el frontend esté disponible

2. **Integration-test**:
   - `maven-failsafe-plugin`: Ejecuta las pruebas de Playwright

3. **Post-integration-test**:
   - Detiene el backend y el frontend

## Configuración Técnica

### Backend

- **Puerto**: 8080
- **Perfil activo**: `e2e-test`
- **Base de datos**: H2 en memoria
- **Datos de prueba**: Script SQL que crea usuarios de prueba

### Frontend

- **Puerto**: 3000
- **Configuración**: Proxy configurado para redirigir peticiones API a backend

### Datos de Prueba

- **Usuario de prueba**: `testinfinia` / `123456`
- **Configuración**: Los datos se reinician en cada ejecución

### Selectores en Playwright

Las pruebas utilizan selectores estables basados en atributos `data-testid`:

```java
// Ejemplo de selector estable
page.getByTestId("login-button").click();
```

## Ejecución de Pruebas

Para ejecutar las pruebas E2E:

```bash
mvn verify -P e2e-test
```

Este comando:

1. Compila todo el proyecto
2. Inicia el backend y frontend
3. Ejecuta las pruebas de Playwright
4. Detiene todos los servicios

## Mejores Prácticas

1. **Selectores Estables**: Usar `data-testid` en lugar de selectores basados en texto
2. **Limpieza Posterior**: Cada prueba debe limpiar sus datos (ej: vaciar el carrito)
3. **Esperas Explícitas**: Usar métodos como `page.waitForSelector()` para sincronización
4. **Pruebas Aisladas**: Cada prueba debe ser independiente y no depender de otras
5. **Mensajes de Diagnóstico**: Incluir logs detallados para facilitar la depuración
