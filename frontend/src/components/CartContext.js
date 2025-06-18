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

  // Al montar, obtener el carrito desde el backend
  useEffect(() => {
    async function fetchCart() {
      try {
        const sessionId = localStorage.getItem("cartSessionId") || "anonymous-" + Math.random().toString(36).substring(2, 15);
        localStorage.setItem("cartSessionId", sessionId);

        // Usar el ID de usuario si está autenticado, de lo contrario usar sessionId
        const userIdParam = currentUser ? currentUser.username : null;
        const sessionIdParam = !currentUser ? sessionId : null;

        // Si tenemos un usuario autenticado y un carrito existente, vincularlos
        if (currentUser && cartId) {
          await linkCartToUser(cartId);
        }

        const data = await getCart(sessionIdParam, userIdParam);
        setCart((data.items || []).map(adaptCartItem));
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
  }, [currentUser, cartId]);

  // Sincronizar el carrito en localStorage para fallback/offline
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  // Añadir producto al carrito y sincronizar con backend
  async function addToCart(product) {
    try {
      // Usar sessionId o userId según el estado de autenticación
      const sessionId = !currentUser ? localStorage.getItem("cartSessionId") : null;
      const userId = currentUser ? currentUser.username : null;
      
      const updatedCart = await addItemToCart(product, sessionId, userId);
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
      // Usar sessionId o userId según el estado de autenticación
      const sessionId = !currentUser ? localStorage.getItem("cartSessionId") : null;
      const userId = currentUser ? currentUser.username : null;
      
      const updatedCart = await removeItemFromCart(itemId, sessionId, userId);
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
      productImageUrl: item.productImageUrl, // <-- ARREGLO: Asegurar que la URL de la imagen se propague
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
        // Usar sessionId o userId según el estado de autenticación
        const sessionId = !currentUser ? localStorage.getItem("cartSessionId") : null;
        const userId = currentUser ? currentUser.username : null;
        
        // Ahora pasamos también el productId real
        const updatedCart = await updateItemQuantity(itemId, quantity, item.productId, sessionId, userId);
        setCart((updatedCart.items || []).map(adaptCartItem));
        setCartId(updatedCart.id); // Sincronizar cartId también
      } catch (err) {
        setCart(prev => prev.map(i => i.id === itemId ? { ...i, quantity } : i));
      }
    }
  }

  // Limpia el carrito tanto en backend como en frontend y fuerza recarga tras un pago exitoso
  async function clearCartAndReload() {
    // Usar sessionId o userId según el estado de autenticación
    const sessionId = !currentUser ? localStorage.getItem("cartSessionId") : null;
    const userId = currentUser ? currentUser.username : null;
    
    // Primero vacía el carrito en el backend
    await clearCartBackend(sessionId, userId);
    setCart([]);
    setCartId(null);
    try {
      // Recargar el carrito desde el backend (debe venir vacío si el backend lo ha eliminado)
      const data = await getCart(sessionId, userId);
      setCart((data.items || []).map(adaptCartItem));
      setCartId(data.id);
    } catch {
      setCart([]);
      setCartId(null);
    }
  }

  function clearCart() {
    setCart([]);
    // Aquí podrías llamar a un endpoint para limpiar el carrito en el backend si existe
  }

  return (
    <CartContext.Provider value={{ cart, cartId, addToCart, removeFromCart, updateQuantity, clearCart, clearCartAndReload }}>
      {children}
    </CartContext.Provider>
  );
}
