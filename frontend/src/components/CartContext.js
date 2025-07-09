import React, { createContext, useContext, useState, useEffect, useCallback } from "react";
import { getCart, addItemToCart, removeItemFromCart, updateItemQuantity, clearCartBackend } from "../cartApi";
import { linkCartToUser } from "../authApi";
import { useAuth } from "./AuthContext";

const CartContext = createContext();

export function useCart() {
  return useContext(CartContext);
}

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);
  const [cartId, setCartId] = useState(null);
  const [isCartInitialized, setIsCartInitialized] = useState(false);
  const { currentUser } = useAuth();

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

  // Al montar, obtener el carrito desde el backend
  useEffect(() => {
    async function fetchCart() {
      try {
        // Si tenemos un usuario autenticado y un carrito existente, vincularlos
        if (currentUser && cartId) {
          await linkCartToUser(cartId);
        }

        const data = await getCart(getSessionId(), getUserId());
        const adaptedItems = (data.items || []).map(adaptCartItem);
        setCart(adaptedItems);
        setCartId(data.id);
      } catch (err) {
        console.error("Error al cargar el carrito:", err);
        // Si falla el backend, intentar recuperar del localStorage como fallback
        try {
          const stored = localStorage.getItem("cart");
          setCart(stored ? JSON.parse(stored) : []);
        } catch {
          setCart([]);
        }
      } finally {
        setIsCartInitialized(true);
      }
    }
    
    if (!isCartInitialized) {
      fetchCart();
    }
  }, [currentUser, cartId, getSessionId, getUserId, isCartInitialized]); // Dependencias corregidas

  // Sincronizar el carrito en localStorage para fallback/offline
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  // Añadir producto al carrito y sincronizar con backend
  async function addToCart(item) {
    try {
      console.log('Enviando al backend:', JSON.stringify(item, null, 2));
      const updatedCart = await addItemToCart(item, getSessionId(), getUserId());
      setCart((updatedCart.items || []).map(adaptCartItem));
      setCartId(updatedCart.id);
    } catch (err) {
      console.error('Error al añadir al carrito:', err);
    }
  }

  // Eliminar producto del carrito y sincronizar con backend
  async function removeFromCart(itemId) {
    try {
      const updatedCart = await removeItemFromCart(itemId, getSessionId(), getUserId());
      setCart((updatedCart.items || []).map(adaptCartItem));
      setCartId(updatedCart.id);
    } catch (err) {
      setCart(prev => prev.filter(item => item.id !== itemId));
    }
  }

  /**
   * Adaptador de items del backend al formato del frontend
   * Maneja diferentes formatos de datos que pueden venir del backend
   */
  function adaptCartItem(item) {
    const adapted = {
      id: item.id,
      productId: item.productId,
      name: item.productName,
      description: '', // La descripción no viene del carrito
      price: item.unitPrice,
      quantity: item.quantity,
      totalPrice: item.unitPrice * item.quantity,
      productImageUrl: item.productImageUrl, // La imagen SÍ viene del carrito
      size: item.size, // Mantener size y type
      type: item.type,
    };
    return adapted;
  }

  // Actualizar cantidad de producto (PUT si >=1, DELETE si 0)
  async function updateQuantity(itemId, quantity) {
    const item = cart.find(i => i.id === itemId);
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
        item.price // unitPrice
      );
      setCart((updatedCart.items || []).map(adaptCartItem));
    }
  }

  // Limpia el carrito tanto en backend como en frontend y fuerza recarga tras un pago exitoso
  async function clearCartAndReload() {
    try {
      await clearCartBackend(getSessionId(), getUserId());
    } catch (error) {
      console.error("Error al limpiar el carrito en el backend:", error);
    }
    setCart([]);
    setCartId(null);
    localStorage.removeItem('cart');
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
  }

  function clearCart() {
    setCart([]);
    // Limpia direcciones guardadas al vaciar el carrito
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
    // Aquí podrías llamar a un endpoint para limpiar el carrito en el backend si existe
  }

  return (
    <CartContext.Provider value={{ cart, cartId, addToCart, removeFromCart, updateQuantity, clearCart, clearCartAndReload }}>
      {children}
    </CartContext.Provider>
  );
}
