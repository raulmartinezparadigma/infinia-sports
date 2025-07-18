# Frontend - Infinia Sports

Este módulo contiene la Single Page Application (SPA) para el e-commerce Infinia Sports. Está desarrollada con React.js y consume la API RESTful proporcionada por el módulo `backend`.

## Tecnologías Clave

-   **React.js**: Librería principal para la construcción de la interfaz de usuario.
-   **React Router**: Para la gestión de rutas y navegación dentro de la aplicación.
-   **Axios**: Cliente HTTP para realizar peticiones a la API del backend.
-   **Material-UI (MUI)**: Biblioteca de componentes de UI para un diseño moderno y consistente.
-   **Jest** y **React Testing Library**: Para las pruebas unitarias y de integración de componentes.

## Arquitectura y Características

### Estructura de Directorios

El código fuente se organiza de la siguiente manera dentro de `src/`:

-   **`api`**: Contiene la configuración de Axios, incluyendo la instancia base y los interceptores.
-   **`components`**: Componentes de React reutilizables (ej. `ProductCard`, `Navbar`).
-   **`context`**: Contiene los React Contexts para la gestión de estado global (`AuthContext`, `CartContext`).
-   **`hooks`**: Hooks personalizados para encapsular lógica reutilizable.
-   **`pages`**: Componentes que representan las páginas completas de la aplicación (ej. `HomePage`, `ProductDetail`).

### Gestión de Estado

La aplicación utiliza React Context para gestionar el estado global de forma eficiente:

-   **`AuthContext`**: Maneja el estado de autenticación del usuario, incluyendo el token JWT y los datos del usuario logueado.
-   **`CartContext`**: Gestiona el estado del carrito de compras, asegurando que la información (items, subtotal, total) esté sincronizada y disponible en toda la aplicación.

### Comunicación con la API

Se utiliza una instancia de **Axios** centralizada para todas las comunicaciones con el backend. Esta instancia incluye un **interceptor de respuesta** que gestiona de forma automática la expiración de tokens JWT:

1.  Detecta cuando una petición falla con un error `401 Unauthorized`.
2.  Utiliza el refresh token para solicitar un nuevo token de acceso al backend.
3.  Reintenta automáticamente la petición original que había fallado, proporcionando una experiencia de usuario fluida y sin interrupciones.

## Pruebas

Los componentes son probados utilizando **Jest** y **React Testing Library**. Para ejecutar las pruebas unitarias y de integración, utiliza el siguiente comando:

```bash
npm test
```

## Cómo Ejecutar el Frontend

Para levantar el frontend en modo de desarrollo, ejecuta los siguientes comandos desde el directorio `frontend`:

```bash
# Instala todas las dependencias del proyecto
npm install

# Inicia el servidor de desarrollo
npm start
```

La aplicación se iniciará y abrirá automáticamente en `http://localhost:3000` en tu navegador.

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

## Guía rápida y troubleshooting
- Usa **Git Bash** en Windows para evitar problemas con scripts npm.
- Si tras el pago el carrito no se vacía, revisa que se esté llamando a `clearCartAndReload` tras el pago exitoso.
- Para errores de cantidad en el carrito, asegúrate de enviar `{id, productId, quantity}` en el body del PUT.
- Si no se muestra el mensaje correcto en la pantalla de confirmación, revisa el paso del método de pago por `state` en React Router.

## Pruebas rápidas
- Añadir producto al carrito, realizar checkout y pago, comprobar vaciado y sincronización del carrito, y recepción de email.

## Convenciones
- Nombres de clases, métodos y variables en **inglés**.
- Comentarios en **español**.
- Seguir estructura y convenciones del proyecto para componentes y estilos.
