// jest.config.js
module.exports = {
  // moduleNameMapper redirige las importaciones de Jest para resolver
  // problemas de compatibilidad con paquetes específicos.
  moduleNameMapper: {
    // Esta regla soluciona el 'Cannot find module' para react-router-dom
    // usando require.resolve para obtener la ruta exacta del paquete.
    '^react-router-dom$': require.resolve('react-router-dom'),
  },
};
