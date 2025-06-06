# Plan del Módulo de Pagos

## Objetivo
Implementar un sistema extensible y seguro para gestionar pagos con Bizum, Redsys y Transferencia bancaria, permitiendo simular el flujo completo (mock) y estar preparado para una integración real futura. El sistema debe seguir buenas prácticas de seguridad, arquitectura limpia y permitir añadir nuevos métodos fácilmente.

## Arquitectura (3 Capas)

### 1. Capa de Presentación (Controller)
- **PagoController.java**
  - `POST /payments/start` - Iniciar proceso de pago (selección de método, validación de pedido, generación de token de pago seguro)
  - `POST /payments/bizum` - Simular/procesar pago con Bizum
  - `POST /payments/redsys` - Simular/procesar pago con Redsys
  - `POST /payments/transfer` - Simular/procesar pago por transferencia
  - `GET /payments/{id}/status` - Consultar estado del pago

### 2. Capa de Servicio (Service)
- **PaymentService.java** (interfaz)
  - Lógica de negocio de pagos, orquestación y validación
- **Implementaciones concretas por método**
  - `BizumPaymentServiceImpl.java`
  - `RedsysPaymentServiceImpl.java`
  - `TransferPaymentServiceImpl.java`
- **Patrón Estrategia**
  - Selector dinámico de la estrategia de pago según el método elegido

### 3. Capa de Persistencia (Repository)
- **PagoRepository.java**
  - Gestión de entidades de pago, logs y trazabilidad
- **OrderRepository.java** (ya existente)
  - Relación entre pedidos y pagos

## Consideraciones de diseño y seguridad
- Uso de DTOs para entrada/salida, sin exponer entidades internas
- Validación exhaustiva de datos y firmas/mock de autenticidad
- Simulación de callbacks/notificaciones externas (Bizum/Redsys)
- Uso de tokens/códigos únicos para cada pago (prevención de duplicidades)
- Preparar endpoints para recibir notificaciones reales en el futuro
- Logging seguro y trazabilidad de todos los intentos de pago
- Preparar mocks de integración real (simulación de respuestas de los proveedores)

## Flujo general
1. **Inicio de pago:**
   - El usuario selecciona método y se valida el pedido.
   - Se genera un identificador/token seguro de pago.
2. **Procesamiento según método:**
   - Se delega al servicio concreto (estrategia) según el método elegido.
   - Se simula la lógica de validación, respuesta y actualización de estado.
   - Se prepara la estructura para integrar APIs reales (Bizum/Redsys) en el futuro.
3. **Consulta de estado:**
   - El usuario o el sistema puede consultar el estado del pago en cualquier momento.
4. **Notificaciones:**
   - Simulación de envío de correos de confirmación tras pago exitoso.

## Extensibilidad
- Añadir un nuevo método de pago solo requiere implementar una nueva estrategia y registrarla en el selector.
- El controlador y la lógica de negocio permanecen homogéneos y desacoplados.

## Siguientes pasos
1. Definir los DTOs y entidades de pago.
2. Implementar la interfaz de servicio y las estrategias mock por cada método.
3. Crear el selector de estrategia y el controlador.
4. Añadir validaciones, logs y mocks de integración real.
5. Documentar los endpoints y el flujo para facilitar la integración futura.

  - Integración con pasarelas de pago
  - Actualización de estado de pedidos
  - Envío de correos de confirmación

### 3. Capa de Persistencia (Repository)
- **PaymentRepository.java**
  - Almacenamiento de información de pagos
  - Actualización de estado de pedidos en MongoDB

## Implementación Detallada

### Bizum Integration
- Configuración de credenciales
- Implementación de flujo de pago
- Manejo de callbacks y notificaciones
- Verificación de estado de pago

---

## Documentación técnica: Endpoint Bizum y flujo de pago

### 1. Endpoint Backend: `/payments/bizum`
- **Método:** POST
- **Propósito:** Procesar y registrar un pago Bizum en MongoDB
- **Request DTO:**
  ```json
  {
    "paymentId": "string", // Identificador único generado en frontend
    "phoneNumber": "string" // Teléfono Bizum (9 dígitos)
  }
  ```
- **Response DTO:**
  ```json
  {
    "paymentId": "string",
    "transactionId": "string", // UUID generado
    "status": "COMPLETED", // O "FAILED" si hay error
    "providerResponse": "string" // Mensaje mock
  }
  ```
- **Persistencia:**
  - Se crea un documento `Payment` en MongoDB con todos los campos relevantes.
  - Estado inicial: `COMPLETED` (mock, configurable para pruebas de error).

### 2. Flujo completo frontend-backend
- **Componente React:** `PaymentSimulator.js`
- Solicita el teléfono Bizum al usuario.
- Llama a `processBizumPayment(paymentId, phoneNumber)` de `cartApi.js`.
- Este servicio hace un POST a `/payments/bizum`.
- El backend valida, registra el pago y responde con el DTO.
- El frontend muestra feedback según el resultado real.

### 3. Ejemplo de uso desde frontend
```js
const response = await processBizumPayment('abc123xyz', '600112233');
// response: { paymentId, transactionId, status, providerResponse }
```

### 4. Notas para desarrollo
- Todos los identificadores, rutas y clases en inglés.
- Comentarios y documentación en español.
- Preparado para añadir lógica real de integración con Bizum en el futuro.
- Consultar pagos: buscar en MongoDB colección `payments`.

### Redsys Integration
- Configuración de credenciales de comercio
- Generación de formularios de pago
- Procesamiento de respuestas
- Verificación de firmas y seguridad

### Bank Transfer Integration
- Generación de instrucciones de pago
- Datos bancarios para transferencia
- Proceso manual de verificación
- Actualización de estado

### Envío de Correos
- Configuración de servidor SMTP
- Plantillas de correo electrónico
- Personalización de mensajes
- Seguimiento de envíos

### Frontend (React.js)
- Interfaces para selección de método de pago
- Formularios específicos para cada método
- Pantallas de confirmación y error
- Redirecciones a pasarelas externas cuando sea necesario

### Manejo de Errores
- Errores de comunicación con pasarelas
- Pagos rechazados o cancelados
- Timeouts y reintentos
- Notificaciones al usuario

## Pruebas
- Pruebas unitarias para cada método de pago
- Pruebas de integración con pasarelas
- Pruebas de envío de correos
- Pruebas de interfaz de usuario

## Dependencias Tecnológicas
- Spring Boot
- JavaMail API
- Bibliotecas específicas para Bizum y Redsys
- MongoDB Driver
- React.js

## Próximos Pasos
1. Configurar integraciones con pasarelas de pago
2. Implementar servicios de pago básicos
3. Configurar envío de correos
4. Desarrollar interfaz de usuario para pagos
5. Integrar con módulo de checkout
6. Realizar pruebas de integración completas
