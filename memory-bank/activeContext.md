# Contexto Activo - Infinia Sports

## Enfoque de Trabajo Actual
Actualmente, el proyecto Infinia Sports se encuentra en fase de planificación y configuración inicial. Se han creado documentos detallados para cada módulo del sistema y se está preparando la estructura base del proyecto.

## Cambios Recientes
- Creación de documentos de planificación para cada módulo del sistema
- Inicialización del repositorio Git
- Configuración básica del entorno de desarrollo (VS Code)
- Creación de la estructura del Memory Bank

## Próximos Pasos
1. Crear la estructura base del proyecto según el plan definido:
   - Estructura de carpetas para backend y frontend
   - Configuración inicial de Spring Boot
   - Configuración inicial de React.js
2. Definir la especificación OpenAPI para la API REST
3. Implementar la entidad Producto y su persistencia en PostgreSQL
4. Configurar conexiones a bases de datos (PostgreSQL y MongoDB)
5. Comenzar con el diseño de la interfaz de usuario

## Decisiones y Consideraciones Activas

### Arquitectura
- Confirmada la arquitectura de 3 capas para el backend
- Confirmada la separación de bases de datos (PostgreSQL para Productos, MongoDB para Pedidos)

### Tecnologías
- Seleccionadas las tecnologías principales: Java 17, Spring Boot, React.js
- Pendiente de decidir entre Material-UI o Bootstrap para componentes UI
- Pendiente de decidir entre Redux o Context API para gestión de estado

### Convenciones de Nomenclatura
- ACTUALIZACIÓN IMPORTANTE: Los nombres de clases, métodos, funciones y variables deben estar en INGLÉS
- Los comentarios del código deben estar en ESPAÑOL
- Se mantienen las convenciones de formato: PascalCase para clases y componentes, camelCase para métodos, funciones y variables

### Implementación
- Prioridad inicial en el Módulo de Productos para establecer la base del sistema
- Enfoque en crear una estructura clara y escalable desde el inicio
- Implementación de importación masiva de productos como funcionalidad clave

### Integración con GitHub
- Repositorio Git inicializado y conectado a GitHub
- Pendiente de subir los archivos de planificación y estructura inicial

## Estado Actual de Módulos

### Módulo de Productos
- Planificación completa
- Pendiente de implementación

### Módulo de Checkout
- Planificación completa
- Pendiente de implementación

### Módulo de Pagos
- Planificación completa
- Pendiente de implementación

### Frontend
- Planificación completa
- Implementación en progreso
- **ACTUALIZACIÓN (11/06/2025)**: Migración completa de pruebas unitarias de Vitest a Jest
  - Eliminadas todas las dependencias y configuraciones de Vitest
  - Configurado Jest con soporte completo para React Testing Library
  - Implementados polyfills necesarios para TextEncoder/TextDecoder y fetch
  - Solucionados problemas con importaciones de archivos estáticos
  - Todos los tests (18) pasan correctamente

## Bloqueos o Impedimentos
- No hay bloqueos identificados actualmente

## Notas Importantes
- Código (clases, métodos, variables): en inglés
- Comentarios y documentación: en español
- Seguir las convenciones de código establecidas (PascalCase para clases/componentes, camelCase para métodos/funciones/variables)
- Mantener la arquitectura de 3 capas en backend
- No realizar cambios que no se soliciten explícitamente
