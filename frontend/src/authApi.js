// Servicio para interactuar con la API REST de autenticación
// Todos los nombres en inglés, comentarios en español
import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';
const AUTH_ENDPOINT = `${API_BASE}/api/auth`;

// Configuración para incluir el token JWT en las peticiones
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Iniciar sesión
export async function login(username, password) {
  try {
    const response = await axios.post(`${AUTH_ENDPOINT}/login`, {
      username,
      password
    });
    return response.data;
  } catch (error) {
    console.error('Error al iniciar sesión:', error);
    throw error;
  }
}

// Registrar un nuevo usuario
export async function register(userData) {
  try {
    const response = await axios.post(`${AUTH_ENDPOINT}/register`, userData);
    return response.data;
  } catch (error) {
    console.error('Error al registrar usuario:', error);
    throw error;
  }
}

// Obtener el usuario actual
export async function getCurrentUser() {
  try {
    const response = await axios.get(`${API_BASE}/api/users/me`);
    return response.data;
  } catch (error) {
    console.error('Error al obtener el usuario actual:', error);
    throw error;
  }
}

// Vincular el carrito con el usuario autenticado
export async function linkCartToUser(cartId) {
  try {
    // El endpoint no necesita body ya que toma el usuario del token JWT
    const response = await axios.put(`${API_BASE}/cart/link/${cartId}`);
    return response.data;
  } catch (error) {
    console.error('Error al vincular el carrito con el usuario:', error);
    throw error;
  }
}
