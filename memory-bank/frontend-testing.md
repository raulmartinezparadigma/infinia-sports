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

## Notas adicionales
Las pruebas siguen las convenciones del proyecto:
- Código en inglés
- Comentarios en español
- Nombres de funciones y variables en camelCase
