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
    const response = await axios.get(`${API_BASE_URL}/api/orders/${orderId}/payment`);
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null;
    }
    throw error;
  }
}

export async function fetchOrderById(orderId) {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/orders/${orderId}`);
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null;
    }
    throw error;
  }
}
