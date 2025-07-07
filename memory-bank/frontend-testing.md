# Configuración de Testing Frontend con Jest

## Descripción
Este documento describe la configuración de pruebas unitarias para el frontend de Infinia Sports utilizando Jest, un framework de testing robusto y ampliamente utilizado compatible con React.

## Fecha de implementación
11/06/2025

## Tecnologías utilizadas
- Jest: Framework de testing
- React Testing Library: Utilidades para probar componentes React
- JSDOM: Simulación del DOM para pruebas
- Babel: Transpilación de código para pruebas

## Estructura de pruebas
Las pruebas siguen la convención de nombrado `*.test.js` y se ubican junto a los componentes que prueban. Esta estructura facilita la localización y mantenimiento de las pruebas.

```
src/
  components/
    ProductCard.js
    ProductCard.test.js
    CartContext.js
    CartContext.test.js
```

## Ejemplos de pruebas implementadas

### Componentes básicos
- **ProductCard**: Pruebas de renderizado y comportamiento básico
- **CartContext**: Pruebas del contexto de carrito y sus funciones

### Componentes complejos
- **PaymentSelector**: Pruebas de integración con múltiples dependencias y flujos de usuario

## Ejecución de pruebas
Se ha creado un script `test-frontend.sh` que facilita la ejecución de las pruebas con diferentes opciones:

1. Ejecutar todos los tests
2. Ejecutar tests con cobertura
3. Ejecutar tests en modo watch
4. Ejecutar tests de componentes específicos

## Configuración técnica
La configuración de Jest se encuentra en el archivo `jest.config.js` y está optimizada para:

- Procesamiento correcto de archivos JS con sintaxis JSX
- Soporte para CSS y archivos estáticos en los tests
- Generación de informes de cobertura
- Integración con React Testing Library
- Configuración de Babel para transpilación de código

## Ventajas de usar Jest
- Mayor compatibilidad con archivos JS que contienen sintaxis JSX
- No requiere cambiar las extensiones de los archivos fuente
- Amplia comunidad y documentación disponible
- Integración nativa con React y Create React App
- Mocks y spies integrados para facilitar las pruebas

## Próximos pasos
- Aumentar la cobertura de pruebas para todos los componentes
- Implementar pruebas de integración para flujos completos
- Configurar pruebas end-to-end con Playwright o Cypress

## Pruebas End-to-End (E2E) con Playwright y Java

### Framework y Configuración
Se ha adoptado **Playwright con Java** como el framework estándar para las pruebas funcionales E2E. Las pruebas residen en un módulo de Maven dedicado (`playwright-tests`) para mantener el código de prueba aislado de la aplicación principal.

### Mejor Práctica: Uso de `data-testid` para Selectores Robustos
Para crear pruebas mantenibles y desacopladas de la implementación del UI, se sigue un patrón estricto:
1.  **Instrumentación en React**: A los elementos interactivos clave (botones, inputs, enlaces, etc.) en los componentes de React se les añade un atributo `data-testid` único.
2.  **Selección en Pruebas**: Las pruebas de Playwright en Java deben usar `page.getByTestId("your-test-id")` para localizar estos elementos. 

**Beneficio**: Este enfoque evita el uso de selectores frágiles como clases CSS, IDs generados dinámicamente o texto, que pueden cambiar frecuentemente y romper las pruebas.

### Patrones de Prueba E2E

#### 1. Manejo de Elementos Ocultos (Dropdowns, Menús)
Cuando un elemento a probar se encuentra dentro de un contenedor que no es visible inicialmente (ej. un menú desplegable), la prueba debe:
1.  Realizar la acción que hace visible el contenedor (ej. `page.getByTestId("user-menu-button").click()`).
2.  Solo después de la acción, buscar y/o afirmar la visibilidad del elemento dentro del contenedor (ej. `assertThat(page.getByTestId("logout-button")).isVisible()`).

#### 2. Verificación de Consistencia de Datos entre Vistas
Para asegurar que los datos no solo se muestran, sino que son correctos a través de diferentes vistas, las pruebas deben:
1.  **Extraer Datos**: En una vista de lista (ej. historial de pedidos), capturar identificadores clave de un registro (ej. ID del pedido y fecha).
2.  **Navegar**: Realizar la acción para ir a la vista de detalle de ese registro.
3.  **Verificar**: En la vista de detalle, afirmar que los datos mostrados coinciden con los extraídos en el primer paso.

#### 3. Estrategia para Pruebas Desactualizadas o Rotas
Si una prueba está fundamentalmente desalineada con el flujo de usuario real:
1.  **Ignorar la lógica antigua**: No intentar parchar la prueba rota.
2.  **Instrumentar el UI**: Añadir los `data-testid` necesarios en el frontend de React.
3.  **Reescribir la Prueba**: Crear una nueva clase de prueba o reescribir la existente desde cero para que siga el flujo de usuario correcto, usando los nuevos `data-testid`.

## Notas adicionales
Las pruebas siguen las convenciones del proyecto:
- Código en inglés
- Comentarios en español
- Nombres de funciones y variables en camelCase
