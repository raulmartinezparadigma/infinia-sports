# Infinia Sports Frontend

Este directorio contiene el frontend del e-commerce Infinia Sports, desarrollado en React.js siguiendo el plan definido en `plans/plan-frontend-react.md`.

## Estructura Inicial
- `src/` — Código fuente principal
- `public/` — Archivos estáticos
- `package.json` — Dependencias y scripts

## Primeros pasos
1. Instala las dependencias con `npm install` o `yarn install`.
2. Inicia la app con `npm start` o `yarn start`.

## Componentes y páginas principales
Consulta el plan en `../plans/plan-frontend-react.md` para la descripción de todos los componentes y páginas a implementar.

## Flujo de pagos y confirmación
- Integrados métodos de pago Bizum, Redsys (tarjeta) y transferencia bancaria.
- Tras cualquier pago, el usuario es dirigido a una pantalla de confirmación única, que muestra mensaje contextual según el método utilizado.
- El método de pago se pasa mediante el state de React Router para mostrar el mensaje adecuado.
- El carrito se vacía y sincroniza tras cualquier pago.
- El flujo de checkout es homogéneo para todos los métodos.

## Integración con el backend (carrito)
- La actualización de cantidad de productos en el carrito usa el endpoint `PUT /cart/items/{id}`.
- El body debe incluir `{ id, productId, quantity }`.
- Si falta `productId`, el backend devuelve error 400 ("El ID del producto es obligatorio").
- Si la cantidad baja a 0, el frontend llama a DELETE, nunca a PUT.
- Verifica que el identificador usado en la URL es el id del item en el carrito (MongoDB), no el productId del catálogo.

---

> Comentarios y nombres de clases/métodos en inglés. Comentarios en español. Estructura y convenciones según las reglas del proyecto.
