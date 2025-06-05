// Servicio para interactuar con la API REST de carrito
// Todos los nombres en inglés, comentarios en español
import axios from 'axios';

axios.defaults.withCredentials = true;
const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Obtiene el carrito actual
export async function getCart() {
  // No se envía User-ID, solo se usa sessionId/cookie
  const response = await axios.get(`${API_BASE}/cart`);
  return response.data;
}

// Añade un producto al carrito
export async function addItemToCart(item) {
  // No se envía User-ID, solo se usa sessionId/cookie
  const response = await axios.post(`${API_BASE}/cart/items`, item);
  return response.data;
}

// Elimina un producto del carrito
export async function removeItemFromCart(itemId) {
  // No se envía User-ID, solo se usa sessionId/cookie
  const response = await axios.delete(`${API_BASE}/cart/items/${itemId}`);
  return response.data;
}

// Actualiza la cantidad de un producto (opcional, si el backend lo soporta)
// export async function updateItemQuantity(itemId, quantity, userId) {
//   const headers = userId ? { 'User-ID': userId } : {};
//   const response = await axios.put(`${API_BASE}/cart/items/${itemId}`, { quantity }, { headers });
//   return response.data;
// }
