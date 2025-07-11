# Plan de Implementación para Pruebas E2E con Playwright (v2)

**Objetivo:** Configurar un entorno de pruebas E2E robusto, aislado y controlado, que no entre en conflicto con servicios locales y utilice un ciclo de vida de aplicación predecible.

### Fase 1: Aislamiento de Dependencias y Configuración

1.  **Perfil de Maven (`e2e-test`) en Backend:**
    *   En el `pom.xml` del backend, dentro del perfil `e2e-test`, excluir dependencias conflictivas como `spring-kafka` y Testcontainers para evitar colisiones con servicios locales.
    *   Configurar el perfil para que, cuando esté activo, se salten los tests unitarios (`skip.unit.tests=true`).

2.  **Perfil de Maven (`e2e-test`) en Frontend:**
    *   En el `pom.xml` del frontend, configurar el perfil `e2e-test` para que se salten los tests de Jest.

3.  **Configuración de Spring (`application-e2e-test.properties`):**
    *   Asegurar que el puerto del servidor está en `8085`.
    *   Desactivar explícitamente la autoconfiguración de servicios no necesarios, como Kafka (`spring.kafka.consumer.enabled=false`, `spring.kafka.producer.enabled=false`).
    *   Asegurar la configuración de la base de datos H2 en memoria y la inicialización con `data-e2e-test.sql`.

### Fase 2: Lanzador de Backend Controlado

1.  **Crear Clase Lanzadora (`InfiniaSportsE2ETestApplication.java`):**
    *   En el módulo `playwright-tests`, crear una clase con un método `main`.
    *   Esta clase usará `SpringApplicationBuilder` para iniciar la aplicación del backend de forma programática, forzando el perfil `e2e-test` y el puerto `8085`.

### Fase 3: Orquestación del Ciclo de Vida con Maven

1.  **Plugin `exec-maven-plugin` en `playwright-tests`:**
    *   **`pre-integration-test`:**
        *   Iniciar el backend ejecutando la nueva clase `InfiniaSportsE2ETestApplication`.
        *   Iniciar el frontend con el script `start:e2e` en el puerto `3002`.
    *   **`integration-test`:** El plugin `maven-failsafe-plugin` ejecutará los tests de Playwright (`*Test.java`).
    *   **`post-integration-test`:** Detener los servidores del backend y el frontend usando `taskkill` sobre los puertos `8080` y `3000`.

### Fase 4: Ejecución

1.  **Comando único:**
    *   Lanzar todo el proceso con: `mvn verify -P e2e-test` desde el directorio raíz.
