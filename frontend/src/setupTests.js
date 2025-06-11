// Configuración para los tests con Jest
// Agrega matchers personalizados para afirmaciones sobre nodos DOM
import '@testing-library/jest-dom';

// Implementaciones de TextEncoder y TextDecoder para react-router-dom
if (typeof TextEncoder === 'undefined') {
  global.TextEncoder = require('util').TextEncoder;
}

if (typeof TextDecoder === 'undefined') {
  global.TextDecoder = require('util').TextDecoder;
}

// Polyfill para fetch API en el entorno de pruebas
global.fetch = jest.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve([]),
    text: () => Promise.resolve(''),
  })
);

// Mock para ResizeObserver que no está disponible en jsdom
class ResizeObserverMock {
  observe() {}
  unobserve() {}
  disconnect() {}
}

global.ResizeObserver = ResizeObserverMock;

// Silenciar advertencias específicas de React
const originalConsoleError = console.error;
console.error = (...args) => {
  if (args[0]?.includes?.('Warning: ReactDOM.render is no longer supported')) return;
  if (args[0]?.includes?.('Warning: The current testing environment is not configured')) return;
  originalConsoleError(...args);
};

// Mock para window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

// Mock para window.scrollTo
window.scrollTo = jest.fn();
