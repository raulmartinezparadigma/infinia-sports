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
  const [cartId, setCartId] = useState(null); // El cartId vendrá siempre del backend
  const [isCartInitialized, setIsCartInitialized] = useState(false);
  const { currentUser, isInitialized: isAuthInitialized } = useAuth();

  // Funciones auxiliares para obtener IDs (memoizadas con useCallback)
  const getUserId = useCallback(() => (currentUser ? currentUser.username : null), [currentUser]);

  const getSessionId = useCallback(() => {
    if (currentUser) return null; // Los usuarios logueados no usan sessionId
    let sessionId = localStorage.getItem("cartSessionId");
    if (!sessionId) {
      sessionId = "anonymous-" + Math.random().toString(36).substring(2, 15);
      localStorage.setItem("cartSessionId", sessionId);
    }
    return sessionId;
  }, [currentUser]);

  // Efecto principal para inicializar y sincronizar el carrito
  useEffect(() => {
    // No hacer nada hasta que el estado de autenticación esté resuelto
    if (!isAuthInitialized) {
      return;
    }

    const initializeCart = async () => {
      setIsCartInitialized(false);
      try {
        const userId = getUserId();
        const sessionId = getSessionId();
        
        console.log(`Inicializando carrito con userId: ${userId} y sessionId: ${sessionId}`);

        // El backend se encarga de encontrar, crear o vincular el carrito correcto
        const data = await getCart(sessionId, userId);

        if (data && data.id) {
          setCart(data);
          setCartId(data.id);
        } else {
          // Si el backend devuelve algo inesperado, reseteamos a un estado seguro
          setCart(initialCartState);
          setCartId(null);
        }
      } catch (err) {
        console.error("Error al inicializar el carrito:", err);
        setCart(initialCartState); // En caso de error, mostrar un carrito vacío
        setCartId(null);
      } finally {
        setIsCartInitialized(true);
      }
    };

    initializeCart();
    
    // Este efecto se ejecutará cada vez que el usuario inicie o cierre sesión
  }, [isAuthInitialized, currentUser, getSessionId, getUserId]);

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
