# Plan de Refactorización: Unificación de Usuarios

**Objetivo:** Unificar las tablas `users` y `admin_users` en una única tabla `users`. Esto eliminará la inconsistencia de datos, simplificará la lógica de autenticación y autorización, y mejorará la mantenibilidad del sistema a largo plazo.

---

### Fase 1: Análisis y Preparación (Realizada)

-   [x] **Identificación del Problema:** Se ha detectado una inconsistencia de datos debido a la existencia de dos tablas de usuarios (`users` y `admin_users`), lo que llevó a soluciones temporales como el "usuario fantasma" para satisfacer las restricciones de clave foránea.
-   [x] **Decisión Arquitectónica:** Se ha decidido unificar ambas tablas en una sola entidad `User`.

---

### Fase 2: Refactorización del Backend

1.  **Modelo de Datos:**
    -   [ ] Modificar la entidad `User.java` para que incluya todos los campos necesarios que actualmente están en `AdminUser.java`.
    -   [ ] Eliminar la entidad `AdminUser.java`.

2.  **Capa de Persistencia:**
    -   [ ] Eliminar la interfaz `AdminUserRepository.java`.
    -   [ ] Actualizar `UserRepository.java` si se necesita alguna consulta específica que antes estuviera en el repositorio de admin.

3.  **Capa de Servicio:**
    -   [ ] Unificar `CustomUserDetailsService.java` y `AdminUserDetailsService.java` en un único servicio que implemente `UserDetailsService`. Este nuevo servicio gestionará la carga de usuarios por email/username sin distinción de su rol.
    -   [ ] Refactorizar `UserService.java` para que opere únicamente sobre la entidad `User` unificada.
    -   [ ] Eliminar los servicios que solo daban soporte a la entidad `AdminUser`.

4.  **Configuración de Seguridad:**
    -   [ ] Simplificar `JwtAuthenticationFilter.java`. La lógica para determinar el `UserDetailsService` a usar basándose en el rol del token (`ROLE_ADMIN`) ya no será necesaria, ya que habrá un único servicio.
    -   [ ] Revisar y actualizar `SecurityConfig.java` para asegurar que las reglas de autorización (`.hasRole("ADMIN")`, `.hasRole("USER")`) sigan funcionando correctamente con el modelo unificado.

5.  **Scripts de Inicialización de Datos:**
    -   [ ] Modificar `data.sql` para insertar tanto usuarios normales como administradores en la tabla `users`, asignando los roles correctos en `user_roles`.
    -   [ ] Eliminar la inserción del "usuario fantasma" del script, ya que la causa raíz del problema quedará resuelta.
    -   [ ] Actualizar `data-test.sql` (para el perfil de pruebas E2E) de la misma manera.

---

### Fase 3: Verificación y Pruebas

1.  **Pruebas Unitarias:**
    -   [ ] Actualizar todas las pruebas unitarias existentes que dependían de `AdminUser`, `AdminUserRepository` o los servicios relacionados.
    -   [ ] Crear nuevas pruebas para el `UserDetailsService` unificado.

2.  **Pruebas de Integración y E2E:**
    -   [ ] Ejecutar la suite completa de pruebas de integración del backend (`mvn verify`).
    -   [ ] Ejecutar la suite completa de pruebas E2E con Playwright (`mvn verify -P e2e-test`) para asegurar que los flujos de login y acceso a funcionalidades por rol (tanto para usuarios como para administradores) siguen funcionando correctamente.

---

### Fase 4: Limpieza Final

-   [ ] Eliminar cualquier fichero `*.java` que haya quedado obsoleto tras la refactorización.
-   [ ] Revisar y eliminar cualquier configuración o propiedad en `application.properties` que fuera específica del antiguo modelo.
-   [ ] Actualizar la memoria del proyecto con la nueva arquitectura de usuarios.
