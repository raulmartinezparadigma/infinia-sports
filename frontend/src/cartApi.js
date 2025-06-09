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

// Actualiza la cantidad de un producto en el carrito
export async function updateItemQuantity(itemId, quantity, productId) {
  // El backend espera un objeto CartItemDTO con productId obligatorio
  const response = await axios.put(
    `${API_BASE}/cart/items/${itemId}`,
    { id: itemId, productId, quantity }
  );
  return response.data;
}

// Guarda la dirección de envío en el backend
export async function saveShippingAddress(cartId, address, sameAsBillingAddress = true) {
  const response = await axios.post(
    `${API_BASE}/checkout/direccion?cartId=${cartId}&sameAsBillingAddress=${sameAsBillingAddress}`,
    address
  );
  return response.data;
}

// Procesa un pago Bizum llamando al backend
// Procesa un pago Bizum llamando al backend
export async function processBizumPayment({ paymentId, orderId, phoneNumber, userId }) {
  // Llama al endpoint real del backend para pagos Bizum
  const response = await axios.post(`${API_BASE}/payments/bizum`, {
    paymentId,
    orderId,
    phoneNumber,
    userId
  });
  return response.data;
}

// Vacía todo el carrito en el backend (DELETE /cart)
export async function clearCartBackend() {
  await axios.delete(`${API_BASE}/cart`);
}

// Confirma el pedido y lo envía al backend
export async function confirmOrder(checkoutData) {
  // Asegurarse de que el email está incluido en el DTO
  if (!checkoutData.email && checkoutData.shippingAddress && checkoutData.shippingAddress.email) {
    checkoutData.email = checkoutData.shippingAddress.email;
  }
  
  console.log('[confirmOrder] Enviando datos de checkout:', checkoutData);
  const response = await axios.post(`${API_BASE}/checkout/confirmar`, checkoutData);
  return response.data;
}
