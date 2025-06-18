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

  /**
   * Adaptador de items del backend al formato del frontend
   * Maneja diferentes formatos de datos que pueden venir del backend
   */
  function adaptCartItem(item) {
    // Verificar que item sea un objeto válido
    if (!item || typeof item !== 'object') {
      console.error('Item inválido recibido en adaptCartItem:', item);
      return {
        id: `error-${Date.now()}`,
        name: 'Error: Producto inválido',
        price: 0,
        quantity: 1,
        totalPrice: 0,
        productImageUrl: ''
      };
    }
    
    console.log('Adaptando item del carrito:', item);
    
    // Función auxiliar para convertir cualquier tipo de dato a número
    const parseNumber = (value) => {
      if (value === null || value === undefined) return 0;
      
      if (typeof value === 'number') {
        return isNaN(value) ? 0 : value;
      }
      
      if (typeof value === 'string') {
        // Eliminar caracteres no numéricos excepto punto decimal
        const cleanedValue = value.replace(/[^0-9.]/g, '');
        const parsedValue = parseFloat(cleanedValue);
        return isNaN(parsedValue) ? 0 : parsedValue;
      }
      
      if (typeof value === 'object') {
        // Manejar objetos BigDecimal de Java
        try {
          if (value.value !== undefined) {
            // Formato típico de BigDecimal serializado
            return parseFloat(value.value) || 0;
          }
          const parsedValue = parseFloat(String(value));
          return isNaN(parsedValue) ? 0 : parsedValue;
        } catch (e) {
          console.error('Error al convertir objeto a número:', value, e);
          return 0;
        }
      }
      
      return 0;
    };
    
    // Obtener precio unitario (priorizar unitPrice sobre price)
    const unitPrice = parseNumber(item.unitPrice);
    const price = parseNumber(item.price);
    const finalPrice = unitPrice > 0 ? unitPrice : price;
    
    // Obtener cantidad (asegurar que sea un número entero positivo)
    const quantity = Math.max(1, parseInt(item.quantity) || 1);
    
    // Calcular precio total (priorizar totalPrice del backend si existe)
    let totalPrice = parseNumber(item.totalPrice);
    if (totalPrice <= 0) {
      totalPrice = finalPrice * quantity;
    }
    
    // Asegurarse de que todos los campos importantes estén presentes
    const productId = item.productId || item.id;
    const itemId = item.id || productId || `temp-${Date.now()}`;
    
    // Crear objeto adaptado con valores normalizados y garantizados
    const result = {
      id: itemId,
      productId: productId,
      name: item.productName || item.name || 'Producto sin nombre',
      description: item.description || '',  // Preservar la descripción del producto
      price: finalPrice,
      quantity: quantity,
      totalPrice: totalPrice,
      productImageUrl: item.productImageUrl || '',
      // Preservar otros campos útiles
      size: item.size,
      type: item.type
    };
    
    // Verificación final para asegurar que price y totalPrice son números válidos
    if (typeof result.price !== 'number' || isNaN(result.price)) {
      console.warn('Price inválido después de adaptación:', result.price);
      result.price = 0;
    }
    
    if (typeof result.totalPrice !== 'number' || isNaN(result.totalPrice)) {
      console.warn('TotalPrice inválido después de adaptación:', result.totalPrice);
      result.totalPrice = 0;
    }
    
    console.log('Item adaptado:', result);
    return result;
  }

  // Actualizar cantidad de producto (PUT si >=1, DELETE si 0)
  async function updateQuantity(itemId, quantity) {
    const item = cart.find(i => i.id === itemId);
    if (!item) return;
    
    // Si la cantidad es menor que 1, eliminar el producto
    if (quantity < 1) {
      await removeFromCart(itemId);
      return;
    }
    
    try {
      // Usar sessionId o userId según el estado de autenticación
      const sessionId = !currentUser ? localStorage.getItem("cartSessionId") : null;
      const userId = currentUser ? currentUser.username : null;
      
      console.log('Actualizando cantidad para item:', item);
      
      // Preparar un objeto completo para la actualización
      const updateData = {
        id: itemId,
        productId: item.productId || item.id,
        quantity: quantity,
        unitPrice: item.price || item.unitPrice,
        description: item.description || '',
        productName: item.name || item.productName
      };
      
      // Enviar la actualización al backend con todos los datos disponibles
      const updatedCart = await updateItemQuantity(
        itemId, 
        quantity, 
        updateData.productId, 
        sessionId, 
        userId,
        updateData.description,
        updateData.productName,
        updateData.unitPrice
      );
      
      // Actualizar el estado local con los datos del backend
      const adaptedItems = (updatedCart.items || []).map(adaptCartItem);
      console.log('Items adaptados después de actualizar cantidad:', adaptedItems);
      setCart(adaptedItems);
      setCartId(updatedCart.id);
    } catch (err) {
      console.error('Error al actualizar cantidad:', err);
      // Fallback: actualizar solo en el frontend
      setCart(prev => prev.map(i => {
        if (i.id === itemId) {
          const totalPrice = i.price * quantity;
          return { ...i, quantity, totalPrice };
        }
        return i;
      }));
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
    // Limpia también localStorage completamente al vaciar el carrito tras pago
    localStorage.removeItem('cart');
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
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
