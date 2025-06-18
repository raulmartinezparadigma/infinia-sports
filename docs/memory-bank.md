# Infinia Sports - Memory Bank

## Funcionalidades implementadas

### Sistema de autenticación
- Registro de usuarios
- Inicio de sesión
- Gestión de tokens JWT
- Protección de rutas

### Catálogo de productos
- Visualización de productos
- Filtrado por categorías
- Búsqueda de productos
- Detalles de producto

### Carrito de compras
- Añadir productos
- Actualizar cantidades
- Eliminar productos
- Persistencia entre sesiones
- Vinculación con usuarios autenticados

### Checkout
- Formulario de dirección de envío
- Formulario de dirección de facturación
- Selección de método de pago
- Confirmación de pedido
- Checkout anónimo (sin necesidad de registro)

### Backend
- Arquitectura de 3 capas (Controller, Service, Repository)
- Integración con MongoDB
- API RESTful
- Documentación con OpenAPI/Swagger

## Actualizaciones

### 17 de junio de 2025 - Implementación de checkout anónimo
- Creación de componente CheckoutOptions para elegir entre checkout anónimo o autenticado
- Modificación de rutas para permitir acceso a checkout, payment y confirmation sin autenticación
- Adaptación de formularios para mostrar mensajes informativos en modo anónimo
- Documentación del flujo de checkout anónimo

### [Fecha anterior] - Integración de autenticación con carrito
- Vinculación de carritos con usuarios autenticados
- Persistencia de datos entre sesiones
- Configuración de JWT en backend
- Interceptor para añadir token automáticamente

### [Fecha anterior] - Implementación del módulo de checkout
- Desarrollo de endpoints para gestión del carrito
- Implementación de lógica de negocio para cálculo de totales e impuestos
- Creación de modelos y DTOs para transferencia de datos
- Configuración de entorno de pruebas

## Decisiones técnicas importantes
- Uso de React con Material-UI para el frontend
- Implementación de Spring Boot para el backend
- MongoDB como base de datos para flexibilidad en el modelo de datos
- Arquitectura de 3 capas para separación de responsabilidades
- Checkout anónimo para mejorar la experiencia de usuario y reducir fricciones en el proceso de compra
