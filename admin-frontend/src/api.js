// api.js
// Módulo para llamadas a la API del backend
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // Ajusta si el backend está en otro puerto

/**
 * Consulta un pedido por su orderId
 * @param {string} orderId
 * @returns {Promise<Object>} Pedido encontrado o null
 */
/**
 * Consulta el método y estado de pago de un pedido por su orderId
 * @param {string} orderId
 * @returns {Promise<Object>} Información de pago (method, status)
 */
export async function fetchPaymentInfoByOrderId(orderId) {
  try {
    // Obtener el token JWT del localStorage
    const token = localStorage.getItem('admin_jwt');
    console.log('[TRACE] fetchPaymentInfoByOrderId - Token JWT obtenido:', token ? 'Token presente' : 'Token ausente');
    
    // Configurar las cabeceras con el token JWT
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    console.log('[TRACE] fetchPaymentInfoByOrderId - Cabeceras configuradas:', headers);
    
    console.log(`[TRACE] fetchPaymentInfoByOrderId - Realizando petición a: ${API_BASE_URL}/api/orders/${orderId}/payment`);
    const response = await axios.get(`${API_BASE_URL}/api/orders/${orderId}/payment`, { headers });
    console.log('[TRACE] fetchPaymentInfoByOrderId - Respuesta recibida:', response.status);
    return response.data;
  } catch (error) {
    console.error('[TRACE] fetchPaymentInfoByOrderId - Error en la petición:', error.response ? error.response.status : error.message);
    if (error.response && error.response.status === 404) {
      return null;
    }
    throw error;
  }
}

export async function fetchOrderById(orderId) {
  try {
    // Obtener el token JWT del localStorage
    const token = localStorage.getItem('admin_jwt');
    console.log('[TRACE] fetchOrderById - Token JWT obtenido:', token ? 'Token presente' : 'Token ausente');
    
    // Configurar las cabeceras con el token JWT
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    console.log('[TRACE] fetchOrderById - Cabeceras configuradas:', headers);
    
    console.log(`[TRACE] fetchOrderById - Realizando petición a: ${API_BASE_URL}/api/orders/${orderId}`);
    const response = await axios.get(`${API_BASE_URL}/api/orders/${orderId}`, { headers });
    console.log('[TRACE] fetchOrderById - Respuesta recibida:', response.status);
    return response.data;
  } catch (error) {
    console.error('[TRACE] fetchOrderById - Error en la petición:', error.response ? error.response.status : error.message);
    if (error.response && error.response.status === 404) {
      return null;
    }
    throw error;
  }
}
