import React, { createContext, useContext, useState, useEffect, useCallback } from "react";
import { getCart, addItemToCart, removeItemFromCart, updateItemQuantity, clearCartBackend } from "../cartApi";
import { linkCartToUser } from "../authApi";
import { useAuth } from "./AuthContext";

const CartContext = createContext();

export function useCart() {
  return useContext(CartContext);
}

const initialCartState = { items: [], subtotal: 0, shippingCost: 0, tax: 0, total: 0 };

export function CartProvider({ children }) {
  const [cart, setCart] = useState(initialCartState);
  const [cartId, setCartId] = useState(null);
  const [isCartInitialized, setIsCartInitialized] = useState(false);
  const { currentUser, isInitialized: isAuthInitialized } = useAuth();

  // Funciones auxiliares para obtener IDs (memoizadas con useCallback)
  const getUserId = useCallback(() => (currentUser ? currentUser.username : null), [currentUser]);

  const getSessionId = useCallback(() => {
    if (currentUser) return null;
    let sessionId = localStorage.getItem("cartSessionId");
    if (!sessionId) {
      sessionId = "anonymous-" + Math.random().toString(36).substring(2, 15);
      localStorage.setItem("cartSessionId", sessionId);
    }
    return sessionId;
  }, [currentUser]);

  // Al montar, obtener el carrito desde el backend, PERO solo si Auth ya está listo
  useEffect(() => {
    async function fetchCart() {
      if (isCartInitialized) return; // Si ya está inicializado, no hacer nada

      try {
        // Si tenemos un usuario autenticado y un carrito existente, vincularlos
        if (currentUser && cartId) {
          await linkCartToUser(cartId);
        }

        const data = await getCart(getSessionId(), getUserId());
        // Asegurarse de que data no es null antes de acceder a sus propiedades
        if (data && data.id) {
            setCart(data);
            setCartId(data.id);
        } else {
            setCart(initialCartState);
            setCartId(null);
        }
      } catch (err) {
        console.error("Error al cargar el carrito:", err);
        // Si falla el backend, intentar recuperar del localStorage como fallback
        try {
          const stored = localStorage.getItem("cart");
          setCart(stored ? JSON.parse(stored) : initialCartState);
        } catch {
          setCart(initialCartState);
        }
      } finally {
        setIsCartInitialized(true);
      }
    }
    
    // Solo ejecutar si Auth ha terminado y el carrito aún no se ha cargado.
    if (isAuthInitialized && !isCartInitialized) {
      fetchCart();
    }
  }, [isAuthInitialized, isCartInitialized, currentUser, cartId, getSessionId, getUserId]);

  // Sincronizar el carrito en localStorage para fallback/offline
  useEffect(() => {
    // Solo guardar en localStorage si el carrito ha sido inicializado desde el backend
    if (isCartInitialized) {
        localStorage.setItem("cart", JSON.stringify(cart));
    }
  }, [cart, isCartInitialized]);

  // Añadir producto al carrito y sincronizar con backend
  async function addToCart(item) {
    try {
      console.log('Enviando al backend:', JSON.stringify(item, null, 2));
      const updatedCart = await addItemToCart(item, getSessionId(), getUserId());
      setCart(updatedCart);
      setCartId(updatedCart.id);
    } catch (err) {
      console.error('Error al añadir al carrito:', err);
    }
  }

  // Eliminar producto del carrito y sincronizar con backend
  async function removeFromCart(itemId) {
    try {
      const updatedCart = await removeItemFromCart(itemId, getSessionId(), getUserId());
      setCart(updatedCart);
      setCartId(updatedCart.id);
    } catch (err) {
      // Fallback optimista: eliminar del estado local si falla el backend
      setCart(prev => ({
        ...prev,
        items: prev.items.filter(item => item.id !== itemId)
        // Nota: los totales no se recalcularán aquí, se necesita un refresh
      }));
    }
  }

  // Actualizar cantidad de producto (PUT si >=1, DELETE si 0)
  async function updateQuantity(itemId, quantity) {
    const item = cart.items.find(i => i.id === itemId);
    if (item) {
      // Pasamos todos los datos del item que necesita el backend en el DTO
      const updatedCart = await updateItemQuantity(
        itemId, 
        quantity, 
        item.productId, 
        getSessionId(), 
        getUserId(),
        item.description, // description
        item.name, // productName
        item.unitPrice // unitPrice
      );
      setCart(updatedCart);
    }
  }

  // Limpia el carrito tanto en backend como en frontend y fuerza recarga tras un pago exitoso
  async function clearCartAndReload() {
    try {
      await clearCartBackend(getSessionId(), getUserId());
    } catch (error) {
      console.error("Error al limpiar el carrito en el backend:", error);
    }
    setCart(initialCartState);
    setCartId(null);
    localStorage.removeItem('cart');
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
  }

  async function clearCart() {
    try {
      // Llamar al backend para que limpie el carrito, pero no usaremos su respuesta directamente.
      await clearCartBackend(getSessionId(), getUserId());
    } catch (error) {
      console.error("Error al vaciar el carrito:", error);
    }
    // Siempre restablecer al estado inicial para garantizar una estructura válida.
    setCart(initialCartState);
    setCartId(null);

    // Limpia direcciones guardadas al vaciar el carrito
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
  }

  return (
    <CartContext.Provider value={{ cart, cartId, addToCart, removeFromCart, updateQuantity, clearCart, clearCartAndReload }}>
      {children}
    </CartContext.Provider>
  );
}
