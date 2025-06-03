# Módulo de Checkout - Infinia Sports

## Descripción

Este módulo implementa la funcionalidad completa de checkout para la tienda online de Infinia Sports, siguiendo una arquitectura de 3 capas (Controller, Service, Repository) y utilizando MongoDB para la persistencia de datos.

## Requisitos

- Java 17
- Maven 3.8+
- MongoDB (ejecutándose en localhost:27017 o configurado en application.properties)

## Estructura del Módulo

El módulo de checkout está compuesto por:

1. **Entidades**:
   - `Order`: Documento MongoDB con estructura compleja para pedidos
   - `Cart`: Documento MongoDB para el carrito de compras

2. **DTOs**:
   - `CartItemDTO`: Para transferencia de datos de ítems del carrito
   - `AddressDTO`: Para direcciones de envío y facturación
   - `CheckoutDTO`: Para el proceso de checkout

3. **Repositorios**:
   - `CartRepository`: Operaciones CRUD para carritos
   - `OrderRepository`: Operaciones CRUD para pedidos

4. **Servicios**:
   - `CheckoutService`: Interfaz del servicio
   - `CheckoutServiceImpl`: Implementación con lógica de negocio

5. **Controladores**:
   - `CheckoutController`: Endpoints REST para gestión del carrito y checkout

## Ejecución de la Aplicación

Para ejecutar la aplicación:

```bash
cd /ruta/a/infinia-sports/backend
mvn spring-boot:run
```

La aplicación se ejecutará en `http://localhost:8080` por defecto.

## Pruebas de los Endpoints

### Usando el Script de Pruebas

Se ha creado un script bash para probar todos los endpoints del módulo:

```bash
# Dar permisos de ejecución al script
chmod +x src/test/resources/checkout-tests.sh

# Ejecutar el script
./src/test/resources/checkout-tests.sh
```

El script realizará las siguientes operaciones:
1. Añadir productos al carrito
2. Obtener el contenido del carrito
3. Eliminar productos del carrito
4. Guardar direcciones de envío
5. Confirmar un pedido
6. Obtener información del pedido

### Pruebas Manuales con Curl

También puedes probar los endpoints manualmente:

#### 1. Añadir un producto al carrito

```bash
curl -X POST "http://localhost:8080/carrito/items" \
  -H "Content-Type: application/json" \
  -H "User-ID: test-user" \
  -d '{
    "productId": "PROD-001",
    "productName": "Balón de fútbol profesional",
    "quantity": 2,
    "unitPrice": 49.99,
    "attributes": {
      "color": "blanco",
      "tamaño": "5"
    }
  }'
```

#### 2. Obtener el contenido del carrito

```bash
curl -X GET "http://localhost:8080/carrito" \
  -H "User-ID: test-user"
```

#### 3. Eliminar un producto del carrito

```bash
curl -X DELETE "http://localhost:8080/carrito/items/{itemId}" \
  -H "User-ID: test-user"
```

#### 4. Guardar dirección de envío

```bash
curl -X POST "http://localhost:8080/checkout/direccion?cartId={cartId}&sameAsBillingAddress=true" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "addressLine1": "Calle Principal 123",
    "city": "Madrid",
    "postalCode": "28001",
    "country": "España",
    "phoneNumber": "+34600000000"
  }'
```

#### 5. Confirmar pedido

```bash
curl -X POST "http://localhost:8080/checkout/confirmar" \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": "{cartId}",
    "email": "juan.perez@example.com",
    "shippingAddress": {
      "firstName": "Juan",
      "lastName": "Pérez",
      "addressLine1": "Calle Principal 123",
      "city": "Madrid",
      "postalCode": "28001",
      "country": "España",
      "phoneNumber": "+34600000000"
    },
    "sameAsBillingAddress": true,
    "shippingMethod": "STANDARD",
    "paymentMethod": "CREDIT_CARD"
  }'
```

#### 6. Obtener información del pedido

```bash
curl -X GET "http://localhost:8080/pedidos/{orderId}"
```

## Pruebas Unitarias

Se han implementado pruebas unitarias para el controlador y el servicio. Para ejecutarlas:

```bash
mvn test
```

## Notas Adicionales

- El módulo utiliza un tipo de IVA fijo del 21%
- Los IDs de pedido se generan con un formato "ORD-" seguido de un timestamp y un UUID
- La gestión de sesiones se realiza mediante cookies y/o el encabezado User-ID
- Las validaciones se realizan en la capa de servicio siguiendo las reglas del proyecto
