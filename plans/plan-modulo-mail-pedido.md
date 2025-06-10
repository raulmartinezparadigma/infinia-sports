# Plan para el Módulo de Envío de Emails de Resumen de Pedido

## 1. Objetivo del Módulo
Enviar automáticamente un correo electrónico al usuario tras completar un pedido, incluyendo un resumen claro y profesional del pedido realizado.

## 2. Opciones de Formato de Email

### Opción A: Texto Plano
- Ventajas: Sencillo de implementar y probar, máxima compatibilidad.
- Desventajas: Menor atractivo visual.
- Ejemplo:
  Hola [Nombre],

  Gracias por tu compra en Infinia Sports.
  Resumen de tu pedido:
  - Pedido: #12345
  - Productos:
    * Zapatillas X (Talla 42) - 1 x 79,99€
    * Camiseta Pro (M) - 2 x 24,99€
  - Total: 129,97€
  - Dirección de envío: [dirección]

  ¡Gracias por confiar en nosotros!

### Opción B: HTML Simple
- Ventajas: Permite formato, tablas y mejor presentación.
- Desventajas: Ligeramente más complejo, pero sigue siendo fácil de probar.
- Ejemplo:
  <h2>¡Gracias por tu compra, [Nombre]!</h2>
  <p>Resumen de tu pedido <b>#12345</b>:</p>
  <table>
    <tr><th>Producto</th><th>Cantidad</th><th>Precio</th></tr>
    <tr><td>Zapatillas X (42)</td><td>1</td><td>79,99€</td></tr>
    <tr><td>Camiseta Pro (M)</td><td>2</td><td>24,99€</td></tr>
  </table>
  <p><b>Total:</b> 129,97€</p>
  <p>Dirección de envío: [dirección]</p>
  <p>¡Gracias por confiar en Infinia Sports!</p>

### Opción C: HTML con Branding
- Ventajas: Imagen corporativa, logos, colores, enlaces.
- Desventajas: Requiere maquetación básica y pruebas en diferentes clientes de correo.

## 3. Opciones Tecnológicas (Java/Spring)

### Opción 1: Spring Boot + JavaMailSender (Spring Email)
- Pros: Integración nativa en Spring, fácil de configurar, soporta texto y HTML.
- Contras: Necesita configuración SMTP (puede usarse Gmail, Mailtrap, etc. para pruebas).

### Opción 2: Bibliotecas externas (Simple Java Mail, Apache Commons Email)
- Pros: APIs sencillas, buena documentación.
- Contras: Añade dependencia externa, menos integración nativa con Spring.

### Opción 3: Servicios externos vía API (SendGrid, Mailgun, etc.)
- Pros: Entrega garantizada, plantillas avanzadas, logs.
- Contras: Requiere cuenta externa y clave API, menos control local.

#### Recomendación para integración sencilla y pruebas:
- Usar JavaMailSender de Spring Boot con configuración SMTP local (Mailtrap recomendado para pruebas sin enviar emails reales).

## 4. Estructura Propuesta del Módulo

- Paquete: com.infinia.sports.mail
- Componentes:
  - OrderMailService (interfaz)
  - OrderMailServiceImpl (implementación)
  - MailConfig (configuración SMTP)
  - Plantillas de email (en recursos, si se usa HTML)
- Integración: Llamada desde el flujo de confirmación de pedido tras el pago.

## 5. Pruebas
- Usar Mailtrap para pruebas locales.
- Añadir logs y mocks para entornos de desarrollo.
