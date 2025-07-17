import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import App from './App';
import * as cartApi from './cartApi';

// Mockear dependencias externas
jest.mock('./cartApi');
const AuthContext = jest.requireActual('./components/AuthContext');

describe('App Integration Test', () => {

  beforeEach(() => {
    // Mockear la API del carrito para que devuelva un carrito vacío
    cartApi.getCart.mockResolvedValue({ id: 'test-cart-id', items: [] });

    // Mockear el hook de autenticación para simular un usuario no logueado pero con contexto inicializado
    const mockAuthContext = {
      currentUser: null,
      isInitialized: true,
      getSession: jest.fn(() => ({ id: 'test-session-id' })),
    };
    jest.spyOn(AuthContext, 'useAuth').mockReturnValue(mockAuthContext);

    // Mockear la llamada fetch para la lista de productos
    jest.spyOn(window, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ([{ id: 1, name: 'Test Product', price: 100 }]),
    });
  });

  afterEach(() => {
    // Restaurar todos los mocks para evitar contaminación entre tests
    jest.restoreAllMocks();
  });

  it('renderiza la aplicación completa con sus providers sin errores', async () => {
    render(<App />);

    // Esperar a que un elemento clave de la UI (del Navbar) esté presente
    await waitFor(() => {
      expect(screen.getByPlaceholderText(/buscar productos/i)).toBeInTheDocument();
    });

    // Verifica que el logo se renderiza (corregido para que coincida con el alt text real)
    expect(screen.getByAltText(/Infinia Sports/i)).toBeInTheDocument();
  });
});
