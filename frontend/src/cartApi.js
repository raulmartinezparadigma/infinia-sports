// Servicio para interactuar con la API REST de carrito
// Todos los nombres en inglés, comentarios en español
import axios from 'axios';

axios.defaults.withCredentials = true;
const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Configura interceptor para añadir token JWT en las peticiones si existe
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Obtiene el carrito actual
export async function getCart(sessionId, userId) {
  let url = `${API_BASE}/api/cart`;
  const params = [];
  const config = { withCredentials: true, headers: {} };

  // Si hay userId, se envía como header; si no, sessionId como query param
  if (userId) {
    config.headers['User-ID'] = userId;
  } else if (sessionId) {
    params.push(`sessionId=${sessionId}`);
  }

  if (params.length > 0) {
    url += `?${params.join('&')}`;
  }

  const response = await axios.get(url, config);
  return response.data;
}

// Añade un producto al carrito
export async function addItemToCart(item, sessionId, userId) {
  let url = `${API_BASE}/api/cart/items`;
  const params = [];
  if (userId) {
    params.push(`userId=${userId}`);
  } else if (sessionId) {
    params.push(`sessionId=${sessionId}`);
  }
  if (params.length > 0) {
    url += `?${params.join('&')}`;
  }
  const response = await axios.post(url, item);
  return response.data;
}

// Elimina un producto del carrito
export async function removeItemFromCart(itemId, sessionId, userId) {
  let url = `${API_BASE}/api/cart/items/${itemId}`;
  const params = [];
  if (userId) {
    params.push(`userId=${userId}`);
  } else if (sessionId) {
    params.push(`sessionId=${sessionId}`);
  }
  if (params.length > 0) {
    url += `?${params.join('&')}`;
  }
  const response = await axios.delete(url);
  return response.data;
}

// Actualiza la cantidad de un producto en el carrito
export async function updateItemQuantity(itemId, quantity, productId, sessionId, userId, description, productName, unitPrice) {
  let url = `${API_BASE}/api/cart/items/${itemId}`;
  const params = [];
  if (userId) {
    params.push(`userId=${userId}`);
  } else if (sessionId) {
    params.push(`sessionId=${sessionId}`);
  }
  if (productId) params.push(`productId=${productId}`);
  if (description) params.push(`description=${encodeURIComponent(description)}`);
  if (productName) params.push(`productName=${encodeURIComponent(productName)}`);
  if (unitPrice) params.push(`unitPrice=${unitPrice}`);
  if (params.length > 0) {
    url += `?${params.join('&')}`;
  }
  const response = await axios.put(url, { quantity });
  return response.data;
}

// Guarda la dirección de envío en el backend
export async function saveShippingAddress(cartId, address, sameAsBillingAddress = true) {
  const response = await axios.post(
    `${API_BASE}/api/checkout/direccion?cartId=${cartId}&sameAsBillingAddress=${sameAsBillingAddress}`,
    address
  );
  return response.data;
}

// Procesa un pago Bizum llamando al backend
// Procesa un pago Bizum llamando al backend
export async function processBizumPayment({ paymentId, orderId, phoneNumber, userId }) {
  // Llama al endpoint real del backend para pagos Bizum
  const bizumUrl = `${API_BASE}/api/payments/bizum`;
  console.log('[cartApi] Attempting to POST to Bizum URL:', bizumUrl);
  const response = await axios.post(bizumUrl, {
    paymentId,
    orderId,
    phoneNumber,
    userId
  });
  return response.data;
}

// Vacía todo el carrito en el backend (DELETE /cart)
export async function clearCartBackend(sessionId, userId) {
  let url = `${API_BASE}/api/cart`;
  const params = [];
  if (userId) {
    params.push(`userId=${userId}`);
  } else if (sessionId) {
    params.push(`sessionId=${sessionId}`);
  }
  if (params.length > 0) {
    url += `?${params.join('&')}`;
  }
  const response = await axios.delete(url);
  return response.data;
}

// Confirma el pedido y lo envía al backend
export async function confirmOrder(checkoutData) {
  // Asegurarse de que el email está incluido en el DTO
  if (!checkoutData.email && checkoutData.shippingAddress && checkoutData.shippingAddress.email) {
    checkoutData.email = checkoutData.shippingAddress.email;
  }
  
  // Obtener el token de autenticación si existe
  const token = localStorage.getItem('authToken');
  if (token) {
    // Decodificar el token JWT para obtener el userId (no verificamos la firma aquí)
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      const payload = JSON.parse(jsonPayload);
      // Añadir el userId al DTO de checkout
      checkoutData.userId = payload.sub; // 'sub' es el estándar para el ID de usuario en JWT
    } catch (e) {
      console.error('Error decodificando el token JWT:', e);
    }
  }
  
  console.log('[confirmOrder] Enviando datos de checkout:', checkoutData);
  const response = await axios.post(`${API_BASE}/api/checkout/confirm`, checkoutData);
  return response.data;
}
