# Plan de Especificación OpenAPI

## Objetivo
Crear una especificación OpenAPI 3.0.x completa que documente todos los endpoints de la API REST para el e-commerce de productos deportivos, siguiendo las mejores prácticas y facilitando la integración entre frontend y backend.

## Estructura de la Especificación

### 1. Información General
- Título: API de E-commerce Infinia Sports
- Versión: 1.0.0
- Descripción: API REST para gestión de productos deportivos, checkout y pagos
- Servidores: Entorno de desarrollo y producción

### 2. Rutas y Endpoints

#### Módulo de Productos
- `GET /api/productos` - Listar todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `POST /api/productos` - Crear nuevo producto
- `PUT /api/productos/{id}` - Actualizar producto existente
- `DELETE /api/productos/{id}` - Eliminar producto
- `POST /api/productos/importar` - Importación masiva de productos

#### Módulo de Checkout
- `POST /api/carrito/items` - Añadir producto al carrito
- `DELETE /api/carrito/items/{id}` - Eliminar producto del carrito
- `GET /api/carrito` - Obtener contenido del carrito
- `POST /api/checkout/direccion` - Guardar dirección de envío/facturación
- `POST /api/checkout/confirmar` - Confirmar pedido y preparar para pago
- `GET /api/pedidos/{id}` - Obtener información de un pedido

#### Módulo de Pagos
- `POST /api/pagos/iniciar` - Iniciar proceso de pago
- `POST /api/pagos/bizum` - Procesar pago con Bizum
- `POST /api/pagos/redsys` - Procesar pago con Redsys
- `POST /api/pagos/transferencia` - Procesar pago por transferencia
- `GET /api/pagos/{id}/estado` - Consultar estado del pago

### 3. Componentes

#### Esquemas (Modelos)
- **Producto**: Definición completa del modelo de producto
- **CarritoItem**: Elemento del carrito de compras
- **Carrito**: Estructura completa del carrito
- **DireccionEnvio**: Datos de dirección de envío
- **DireccionFacturacion**: Datos de dirección de facturación
- **Order**: Estructura completa del pedido
- **PagoRequest**: Solicitud de inicio de pago
- **PagoResponse**: Respuesta de procesamiento de pago
- **EstadoPago**: Estado actual de un pago

#### Parámetros
- Definición de parámetros comunes (ID, filtros, paginación)

#### Respuestas
- Respuestas estándar (200, 201, 400, 404, 500)
- Respuestas específicas para cada endpoint

#### Seguridad
- Definición de esquemas de seguridad (si aplica)

### 4. Ejemplos
- Ejemplos de solicitudes y respuestas para cada endpoint
- Ejemplos de casos de error comunes

## Formato y Herramientas
- Formato YAML para la especificación
- Generación de documentación interactiva con Swagger UI
- Posibilidad de generación de código cliente/servidor

## Consideraciones Especiales
- Manejo consistente de errores
- Versionado de la API
- Paginación para colecciones grandes
- Filtrado y ordenación

## Próximos Pasos
1. Crear estructura básica de la especificación OpenAPI
2. Definir esquemas de datos principales
3. Documentar endpoints del módulo de Productos
4. Documentar endpoints del módulo de Checkout
5. Documentar endpoints del módulo de Pagos
6. Añadir ejemplos y descripciones detalladas
7. Validar la especificación
8. Integrar con herramientas de generación de código/documentación
