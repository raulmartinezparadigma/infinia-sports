# Implementación de Checkout Anónimo

## Descripción
Se ha implementado la funcionalidad de checkout anónimo que permite a los usuarios completar sus compras sin necesidad de registrarse o iniciar sesión. Esta funcionalidad mejora la experiencia de usuario al eliminar barreras en el proceso de compra.

## Cambios realizados

### Frontend

1. **Componentes nuevos:**
   - `CheckoutOptions.js`: Componente que muestra opciones para continuar con el checkout como usuario anónimo o iniciar sesión/registrarse.

2. **Modificaciones en componentes existentes:**
   - `App.js`: 
     - Creación de componente `CheckoutRoute` que permite tanto usuarios autenticados como anónimos.
     - Actualización de rutas `/checkout`, `/payment` y `/confirmation` para usar `CheckoutRoute` en lugar de `ProtectedRoute`.
   
   - `Checkout.js`:
     - Añadido estado para controlar si el checkout es anónimo.
     - Implementación de paso inicial para seleccionar modo de checkout.
     - Propagación de la propiedad `isAnonymous` a los componentes hijos.
   
   - `ShippingForm.js`, `PaymentSelector.js`, `PaymentConfirmation.js`:
     - Añadidos mensajes informativos para usuarios anónimos.
     - Adaptación de la lógica para funcionar correctamente con checkout anónimo.

### Backend

El backend ya estaba preparado para manejar checkout anónimo gracias a:
- Soporte para carritos basados en `sessionId` (usuarios anónimos) y `userId` (usuarios autenticados).
- El método `confirmOrder` en `CheckoutServiceImpl` que puede procesar pedidos tanto de usuarios autenticados como anónimos.
- La gestión de direcciones de envío y facturación que no requiere autenticación.

## Flujo de usuario

1. El usuario añade productos al carrito.
2. Al ir al checkout, se le presentan dos opciones:
   - Continuar como usuario anónimo
   - Iniciar sesión o registrarse
3. Si elige continuar como anónimo:
   - Se muestra un mensaje informativo en cada paso del checkout.
   - Puede completar todo el proceso sin necesidad de crear una cuenta.
   - Al finalizar, se le ofrece la opción de crear una cuenta para ver su historial de pedidos.
4. Si ya está autenticado, se salta la pantalla de opciones y va directamente al checkout.

## Consideraciones técnicas

- Los pedidos anónimos se identifican mediante `sessionId` en lugar de `userId`.
- Se mantiene la validación de datos en todos los formularios.
- El email es obligatorio para todos los pedidos (autenticados y anónimos) para poder enviar confirmaciones.
- La implementación es compatible con el sistema de autenticación existente.

## Fecha de implementación
17 de junio de 2025
