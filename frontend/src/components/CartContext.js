import React, { createContext, useContext, useState, useEffect } from "react";
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
  const { currentUser } = useAuth();

  // Funciones auxiliares para obtener IDs
  const getUserId = () => (currentUser ? currentUser.username : null);
  const getSessionId = () => {
    if (currentUser) return null;
    let sessionId = localStorage.getItem("cartSessionId");
    if (!sessionId) {
      sessionId = "anonymous-" + Math.random().toString(36).substring(2, 15);
      localStorage.setItem("cartSessionId", sessionId);
    }
    return sessionId;
  };

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
      }
    }
    
    fetchCart();
  }, [currentUser]); // Dependencia simplificada

  // Sincronizar el carrito en localStorage para fallback/offline
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  // Añadir producto al carrito y sincronizar con backend
  async function addToCart(item) {
    try {
      const currentCartId = cartId || (await getCart(getSessionId(), getUserId())).id;
      if (!currentCartId) {
        console.error("No se pudo obtener o crear un ID de carrito.");
        return;
      }
      const updatedCart = await addItemToCart(currentCartId, item.id, item.quantity, item.price);
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
      productImageUrl: '', // La imagen no viene del carrito
      size: item.size, // Mantener size y type
      type: item.type,
    };
    return adapted;
  }

  // Actualizar cantidad de producto (PUT si >=1, DELETE si 0)
  async function updateQuantity(itemId, quantity) {
    const item = cart.find(i => i.id === itemId);
    if (item) {
      const updatedCart = await updateItemQuantity(itemId, quantity, item.productId);
      setCart((updatedCart.items || []).map(adaptCartItem));
    }
  }

  // Limpia el carrito tanto en backend como en frontend y fuerza recarga tras un pago exitoso
  async function clearCartAndReload() {
    await clearCartBackend(getSessionId(), getUserId());
    setCart([]);
    setCartId(null);
    localStorage.removeItem('cart');
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
    try {
      const data = await getCart(getSessionId(), getUserId());
      setCart((data.items || []).map(adaptCartItem));
      setCartId(data.id);
    } catch {
      setCart([]);
      setCartId(null);
    }
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
