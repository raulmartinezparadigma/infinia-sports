# Cuaderno de Bitácora: Pruebas E2E con Playwright

Este documento registra los principales desafíos, problemas y soluciones encontrados durante la implementación y mantenimiento de las pruebas end-to-end con Playwright para Infinia Sports.

## Historial de Incidencias

### Julio 2025 - Configuración Inicial

**Problema**: Diferencias entre entornos de desarrollo y CI
- **Descripción**: Las pruebas funcionaban en local pero fallaban en CI.
- **Causa**: Distintas versiones de navegadores y dependencias.
- **Solución**: Estandarización de versiones mediante el archivo POM y configuración explícita de versiones de Playwright.

### Julio 2025 - Selectores Frágiles

**Problema**: Pruebas inestables por cambios en textos de la interfaz
- **Descripción**: Cambios en etiquetas de botones y textos rompían las pruebas.
- **Causa**: Uso de selectores basados en texto como `page.getByText("Login")`.
- **Solución**: Modificar componentes React para añadir `data-testid` y usar `page.getByTestId()` en las pruebas.

### Julio 2025 - Limpieza del Carrito en Pruebas

**Problema**: Datos residuales entre ejecuciones de pruebas
- **Descripción**: Productos añadidos al carrito permanecían entre ejecuciones, causando fallos.
- **Causa**: Falta de limpieza del carrito al finalizar las pruebas.
- **Solución**: Implementación de limpieza mediante llamada al endpoint DELETE `/api/cart` al final de cada prueba.

### Julio 2025 - Problema de Puerto Backend

**Problema**: Error de conexión al limpiar el carrito
- **Descripción**: La prueba intentaba conectarse al puerto 8085 a pesar de estar configurado el 8080.
- **Causa**: Discrepancia entre la configuración y la documentación. Posible caché o variable de entorno.
- **Solución**: Verificación y unificación de la configuración de puertos. Implementación de logs de diagnóstico.

### Junio 2025 - Errores de Timing

**Problema**: Fallos inconsistentes en pruebas por condiciones de carrera
- **Descripción**: Las pruebas fallaban aleatoriamente al no encontrar elementos que deberían estar visibles.
- **Causa**: Falta de sincronización adecuada con eventos asíncronos (carga de React, peticiones API).
- **Solución**: Implementación de esperas explícitas y correctas:
  ```java
  // Antes (problemático)
  page.click("#submit-button");
  
  // Después (robusto)
  page.click("#submit-button");
  page.waitForSelector(".confirmation-message");
  ```

### Junio 2025 - Problemas de Autenticación

**Problema**: Sesiones inconsistentes durante las pruebas
- **Descripción**: Algunas pruebas fallaban por pérdida de sesión o fallos al iniciar sesión.
- **Causa**: La gestión de cookies y tokens no era consistente entre pruebas.
- **Solución**: Centralización de la lógica de login en `BaseTest` y mejora del manejo de contexto del navegador.

### Julio 2025 - Integración con Maven

**Problema**: Complejidad en la orquestación de servicios
- **Descripción**: Dificultad para iniciar/detener correctamente backend y frontend.
- **Causa**: Configuración compleja con múltiples plugins y dependencias.
- **Solución**: Creación de perfil Maven dedicado (`e2e-test`) con ciclo de vida completo y scripts auxiliares.

### Julio 2025 - Base de Datos para Pruebas

**Problema**: Contaminación de datos en entorno de desarrollo
- **Descripción**: Las pruebas modificaban datos reales en la base de datos de desarrollo.
- **Causa**: Uso de la misma configuración de base de datos para desarrollo y pruebas.
- **Solución**: Implementación de base de datos H2 en memoria con datos preconfigurados para pruebas.

## Lecciones Aprendidas

1. **Selectores Robustos**: Los selectores basados en `data-testid` son mucho más estables que los basados en texto o estructura DOM.

2. **Aislamiento de Pruebas**: Cada prueba debe comenzar con un estado limpio y predecible, y debe limpiar después de sí misma.

3. **Logs de Diagnóstico**: La incorporación de logs detallados facilita enormemente la depuración de fallos.

4. **Sincronización Adecuada**: Las esperas explícitas son cruciales para manejar la naturaleza asíncrona de las aplicaciones web modernas.

5. **Configuración Unificada**: Mantener una única fuente de verdad para la configuración (puertos, URLs, etc.) evita inconsistencias.

## Próximos Pasos y Mejoras

1. **Paralelización de Pruebas**: Implementar ejecución en paralelo para reducir tiempos de CI.

2. **Capturas de Pantalla Automáticas**: Configurar captura automática de screenshots en caso de fallos.

3. **Reportes Visuales**: Integrar generación de informes HTML detallados con capturas de cada paso.

4. **Pruebas de Accesibilidad**: Añadir validaciones de accesibilidad web durante las pruebas E2E.

5. **Monitoreo de Rendimiento**: Incorporar métricas de rendimiento básicas durante la ejecución de pruebas.
