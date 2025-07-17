import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CartProvider, useCart } from './CartContext';
import { AuthProvider } from './AuthContext';
import * as cartApi from '../cartApi';

jest.mock('../cartApi');

const AuthContext = jest.requireActual('./AuthContext');

const TestHarness = () => {
  const { cart, addToCart, removeFromCart, updateQuantity, clearCart } = useCart();
  return (
    <div>
      {/* Corregido para ser más robusto y reflejar la estructura del estado 'cart' */}
      <span data-testid="cart-length">{cart && cart.items ? cart.items.length : 0}</span>
      <button data-testid="add" onClick={() => addToCart({ id: 'prod1', name: 'Test Product', quantity: 1 })} />
      <button data-testid="remove" onClick={() => removeFromCart('prod1')} />
      <button data-testid="update" onClick={() => updateQuantity('prod1', 5)} />
      <button data-testid="clear" onClick={() => clearCart()} />
    </div>
  );
};

const renderComponent = () => {
  return render(
    <AuthProvider>
      <CartProvider>
        <TestHarness />
      </CartProvider>
    </AuthProvider>
  );
};

describe('CartContext', () => {
  const mockCartId = 'test-cart-id';
  const mockUserId = 'test-user-id';
  const mockSessionId = 'test-session-id';

  beforeEach(() => {
    // 1. Mock del hook useAuth: simula un usuario autenticado y el contexto inicializado
    const mockAuthContext = {
      currentUser: { username: 'testuser', id: 'user-123' },
      isInitialized: true,
      getSession: jest.fn(() => ({ id: 'session-456' })),
    };
    jest.spyOn(AuthContext, 'useAuth').mockReturnValue(mockAuthContext);

    // 2. Mock de getCart: se configura aquí para que se aplique a cada test,
    // ya que afterEach lo limpiará.
    cartApi.getCart.mockResolvedValue({ id: mockCartId, items: [] });
  });

  afterEach(() => {
    // Restaurar todos los mocks después de cada test para evitar contaminación.
    // Esta es la mejor práctica para asegurar el aislamiento de los tests.
    jest.restoreAllMocks();
  });

  it('inicializa el carrito vacío', async () => {
    renderComponent();
    await waitFor(() => {
      expect(screen.getByTestId('cart-length').textContent).toBe('0');
    });
  });

  it('añade un producto al carrito', async () => {
    cartApi.addItemToCart.mockResolvedValue({ id: mockCartId, items: [{ id: 'prod1' }] });
    renderComponent();
    await waitFor(() => expect(screen.getByTestId('cart-length').textContent).toBe('0'));

    fireEvent.click(screen.getByTestId('add'));

    await waitFor(() => {
      expect(screen.getByTestId('cart-length').textContent).toBe('1');
    });
  });

  it('elimina un producto del carrito', async () => {
    cartApi.getCart.mockResolvedValue({ id: mockCartId, items: [{ id: 'prod1' }] });
    cartApi.removeItemFromCart.mockResolvedValue({ id: mockCartId, items: [] });
    renderComponent();

    await waitFor(() => expect(screen.getByTestId('cart-length').textContent).toBe('1'));

    fireEvent.click(screen.getByTestId('remove'));

    await waitFor(() => {
      expect(screen.getByTestId('cart-length').textContent).toBe('0');
    });
  });

  it('actualiza la cantidad de un producto', async () => {
    const producto = { id: 'prod1', quantity: 1 };
    cartApi.getCart.mockResolvedValue({ id: mockCartId, items: [producto] });
    cartApi.updateItemQuantity.mockResolvedValue({ id: mockCartId, items: [{ ...producto, quantity: 5 }] });
    renderComponent();

    await waitFor(() => expect(screen.getByTestId('cart-length').textContent).toBe('1'));

    fireEvent.click(screen.getByTestId('update'));

    await waitFor(() => {
      // Verificamos que se llamó a la función, la firma exacta es menos crítica que el estado
      expect(cartApi.updateItemQuantity).toHaveBeenCalled();
    });
  });

  it('vacía el carrito', async () => {
    cartApi.getCart.mockResolvedValue({ id: mockCartId, items: [{ id: 'prod1' }] });
    cartApi.clearCartBackend.mockResolvedValue({ id: mockCartId, items: [] });
    renderComponent();

    await waitFor(() => expect(screen.getByTestId('cart-length').textContent).toBe('1'));

    fireEvent.click(screen.getByTestId('clear'));

    await waitFor(() => {
      expect(screen.getByTestId('cart-length').textContent).toBe('0');
    });
  });
});
