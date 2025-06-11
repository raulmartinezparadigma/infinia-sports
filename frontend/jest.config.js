// Configuración de Jest para pruebas de frontend
module.exports = {
  // Entorno de prueba para React
  testEnvironment: 'jsdom',
  
  // Archivos de configuración global para pruebas
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
  
  // Transformaciones para procesar JSX en archivos .js
  transform: {
    '^.+\\.(js|jsx)$': 'babel-jest'
  },
  
  // Módulos a transformar (importante para procesar JSX en archivos .js)
  transformIgnorePatterns: [
    '/node_modules/(?!(@mui|react-router-dom)/)'
  ],
  
  // Mapeo de módulos para manejar importaciones de archivos no-JavaScript
  moduleNameMapper: {
    '\\.(css|less|sass|scss)$': '<rootDir>/src/__mocks__/styleMock.js',
    '\\.(gif|ttf|eot|svg|png|jpg|jpeg|webp)$': '<rootDir>/src/__mocks__/fileMock.js'
  },
  
  // Ignorar advertencias de MUI Grid props obsoletas
  setupFiles: ['<rootDir>/src/jestSetup.js']
};
