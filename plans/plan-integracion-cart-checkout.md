# Plan de Integración Frontend-Backend: Gestión de Cart y Checkout

## 1. Análisis y Preparación

### 1.1. Revisión de Estructura Actual
- Revisar la estructura y propiedades actuales del Cart en el frontend (`Cart.js`, componentes relacionados, estado global/Redux/Context).
- Revisar el `CartController` y los DTOs asociados en el backend para asegurar que toda la información necesaria está contemplada (productos, cantidades, usuario, etc.).
- Identificar si existe alguna discrepancia o carencia de propiedades entre el formulario del frontend y los DTOs/backend.

### 1.2. Validación de Endpoints
- Confirmar que el endpoint de API para el carrito está correctamente definido y operativo (por ejemplo, `/cart/items`, `/cart`).
- Verificar los métodos HTTP requeridos (GET para obtener, POST para agregar/actualizar, DELETE para eliminar).

---

## 2. Diseño del Formulario y Flujo de Datos en el Frontend

### 2.1. Diseño del Formulario en Cart.js
- Incluir un formulario en `Cart.js` que permita enviar todos los datos del carrito al backend (productos, cantidades, usuario, etc.).
- Asegurarse de que los nombres de los campos coincidan con los esperados por el DTO del backend.
- Añadir validación básica en el frontend (campos requeridos, tipos de datos).

### 2.2. Integración con el Backend
- Utilizar Axios o fetch para enviar los datos al endpoint del carrito.
- Gestionar la respuesta del backend (éxito, errores de validación, etc.).
- Mostrar mensajes claros al usuario en función de la respuesta.

---

## 3. Sincronización y Pruebas de Integración

### 3.1. Pruebas Manuales
- Probar el flujo completo: agregar productos al carrito, enviar el formulario y verificar que los datos llegan correctamente al backend.
- Revisar la persistencia y recuperación del carrito desde el backend.

### 3.2. Manejo de Errores
- Implementar manejo de errores en el frontend para respuestas 400, 404, etc.
- Mostrar mensajes de error útiles y en español.

---

## 4. Documentación y Actualización Continua

### 4.1. Documentar el Flujo
- Documentar el flujo de integración y las propiedades esperadas en los DTOs.
- Mantener actualizado el plan en la carpeta `plans` ante cualquier cambio o nueva indicación.

### 4.2. Actualización Iterativa
- Ante cualquier cambio en los DTOs o en el formulario, revisar y actualizar el plan antes de implementar.

---

## 5. Siguientes Pasos (Preparación para Checkout)
- Una vez validada la integración del carrito, replicar el enfoque para la pantalla `Checkout.js` y el `CheckoutController`, asegurando la correcta transferencia de datos y almacenamiento en los DTOs correspondientes.

---

## Actualización: Cambio de Endpoint de "carrito" a "cart"

### Impacto del Cambio
- Todos los endpoints que usaban la ruta `/carrito` pasarán a ser `/cart`.
- Es necesario actualizar tanto las anotaciones `@RequestMapping`, `@PostMapping`, `@GetMapping`, etc., en el backend, como cualquier referencia en la documentación OpenAPI y en el frontend (llamadas Axios/fetch).
- El DTO, la lógica de negocio y los nombres de variables y clases NO cambian, solo la ruta de los endpoints.

### Pasos Detallados

#### Backend (Spring Boot)
- Buscar y reemplazar todas las rutas que incluyan `/carrito` por `/cart` en los controladores (principalmente en `CheckoutController`).
- Actualizar las anotaciones de Swagger/OpenAPI para reflejar el nuevo path.
- Revisar los tests (si existen) para que apunten a la nueva ruta.

#### Frontend (React)
- Actualizar todas las llamadas a la API que apunten a `/carrito` para que usen `/cart`.
- Revisar los servicios, hooks o utilidades que gestionen la comunicación con el backend para mantener la coherencia.

#### Documentación
- Actualizar la documentación técnica y de usuario para reflejar el cambio de ruta.
- Añadir una nota en el plan sobre este cambio para futuras referencias.

#### Pruebas de Integración
- Verificar que el flujo de añadir, visualizar y eliminar ítems del carrito funciona correctamente con la nueva ruta.
- Validar que no hay referencias rotas ni en el frontend ni en el backend.

---

### Notas Importantes
- No realizar ningún cambio en los DTOs o formularios sin previa revisión y consulta si surge alguna duda sobre las propiedades.
- Mantener la homogeneidad en el código y evitar importaciones con `*`.
- Seguir la convención: nombres en inglés, comentarios en español.
