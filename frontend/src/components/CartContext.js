import React, { createContext, useContext, useState, useEffect } from "react";
import { getCart, addItemToCart, removeItemFromCart, updateItemQuantity } from "../cartApi";

const CartContext = createContext();

export function useCart() {
  return useContext(CartContext);
}

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);
const [cartId, setCartId] = useState(null);
  // Opcional: User-ID para usuarios autenticados
  const userId = null; // Sustituir por lógica real si se implementa autenticación

  // Al montar, obtener el carrito desde el backend
  useEffect(() => {
    async function fetchCart() {
      try {
        const data = await getCart(userId);
        // Adaptar los items al formato esperado por el frontend
        setCart((data.items || []).map(adaptCartItem));
        setCartId(data.id); // Guardar cartId del backend
      } catch (err) {
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
  }, [userId]);

  // Sincronizar el carrito en localStorage para fallback/offline
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  // Añadir producto al carrito y sincronizar con backend
  async function addToCart(product) {
    try {
      const updatedCart = await addItemToCart(product, userId);
      // Adaptar los items al formato esperado por el frontend
      setCart((updatedCart.items || []).map(adaptCartItem));
      setCartId(updatedCart.id); // Sincronizar cartId también
    } catch (err) {
      // Fallback en caso de error
      setCart(prev => {
        const idx = prev.findIndex(item => item.id === product.id);
        if (idx !== -1) {
          const updated = [...prev];
          updated[idx].quantity += 1;
          return updated;
        }
        return [...prev, { ...product, quantity: 1 }];
      });
    }
  }

  // Eliminar producto del carrito y sincronizar con backend
  async function removeFromCart(itemId) {
    try {
      const updatedCart = await removeItemFromCart(itemId, userId);
      // Adaptar los items al formato esperado por el frontend
      setCart((updatedCart.items || []).map(adaptCartItem));
      setCartId(updatedCart.id); // Sincronizar cartId también
    } catch (err) {
      setCart(prev => prev.filter(item => item.id !== itemId));
    }
  }

  // Adaptador de items del backend al formato del frontend
  function adaptCartItem(item) {
    return {
      ...item,
      name: item.productName || item.name || item.description,
      price: item.unitPrice ?? item.price,
      id: item.id,
    };
  }

  // Actualizar cantidad de producto (PUT si >=1, DELETE si 0)
  async function updateQuantity(itemId, quantity) {
    const item = cart.find(i => i.id === itemId);
    if (!item) return;
    if (quantity < 1) {
      await removeFromCart(itemId);
    } else {
      try {
        // Ahora pasamos también el productId real
        const updatedCart = await updateItemQuantity(itemId, quantity, item.productId);
        setCart((updatedCart.items || []).map(adaptCartItem));
        setCartId(updatedCart.id); // Sincronizar cartId también
      } catch (err) {
        setCart(prev => prev.map(i => i.id === itemId ? { ...i, quantity } : i));
      }
    }
  }

  function clearCart() {
    setCart([]);
    // Aquí podrías llamar a un endpoint para limpiar el carrito en el backend si existe
  }

  return (
    <CartContext.Provider value={{ cart, cartId, addToCart, removeFromCart, updateQuantity, clearCart }}>
      {children}
    </CartContext.Provider>
  );
}
