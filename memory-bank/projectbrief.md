# Infinia Sports - E-commerce de Productos Deportivos

## Objetivo del Proyecto
Desarrollar una plataforma de comercio electrónico especializada en productos deportivos, con una experiencia de usuario fluida y un proceso de compra optimizado.

## Requisitos Principales

### Tecnologías
- **Backend**: Java 17 con arquitectura de 3 capas (Controller, Service, Persistence)
- **Frontend**: React.js con estilos divertidos pero corporativos
- **Bases de datos**: PostgreSQL para Productos, MongoDB para Pedidos
- **Documentación API**: OpenAPI 3.0.x

### Módulos Principales
1. **Módulo de Productos**: CRUD completo e importación masiva
2. **Módulo de Checkout**: Gestión de carrito y proceso de compra
3. **Módulo de Pagos**: Integración con Bizum, Redsys y Transferencia bancaria

### Entidades Principales
- **Producto** (PostgreSQL): id, tipo, descripcion, precio, talla
- **Order** (MongoDB): Estructura JSON compleja para pedidos

### Requisitos Adicionales
- Envío de correo de confirmación tras completar pedido
- Manejo básico de errores (400, 404)
- Implementación únicamente de lo especificado

## Alcance del Proyecto
El proyecto se centra en crear una plataforma funcional para la venta de productos deportivos con un enfoque en la experiencia de usuario y la eficiencia en el proceso de compra. No incluye sistema de usuarios ni gestión de inventario en esta fase.

## Restricciones y Consideraciones
- Los nombres de clases, métodos, funciones y variables deben estar en inglés
- Los comentarios del código deben estar en español
- Mantener homogeneidad en el código
- No incluir * en las importaciones
- No realizar cambios que no se soliciten explícitamente
