import React, { createContext, useContext, useState, useEffect } from "react";
import { login, register, getCurrentUser, linkCartToUser } from "../authApi";

// Contexto de autenticación
const AuthContext = createContext();

// Hook personalizado para acceder al contexto de autenticación
export function useAuth() {
  return useContext(AuthContext);
}

// Proveedor del contexto de autenticación
export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Al montar el componente, verificar si hay un usuario autenticado
  useEffect(() => {
    async function fetchCurrentUser() {
      try {
        // Comprobar si hay un token en localStorage
        const token = localStorage.getItem("authToken");
        
        if (token) {
          // Si hay token, intentar obtener el usuario actual
          const userData = await getCurrentUser();
          setCurrentUser(userData);
        }
      } catch (err) {
        console.error("Error al obtener el usuario actual:", err);
        // Si hay un error, limpiar el token
        localStorage.removeItem("authToken");
      } finally {
        setLoading(false);
      }
    }
    
    fetchCurrentUser();
  }, []);
  
  // Función para iniciar sesión
  async function handleLogin(username, password) {
    try {
      setError(null);
      const response = await login(username, password);
      
      // Guardar el token en localStorage
      localStorage.setItem("authToken", response.token);
      
      // Actualizar el estado del usuario
      setCurrentUser({
        username: response.username,
        email: response.email,
        roles: response.roles
      });
      
      // Si hay un carrito en localStorage, vincularlo con el usuario
      const cartId = localStorage.getItem("cartId");
      if (cartId) {
        try {
          // Vincular el carrito con el usuario autenticado
          await linkCartToUser(cartId);
          console.log("Carrito vinculado exitosamente con el usuario", response.username);
        } catch (linkError) {
          console.error("Error al vincular el carrito con el usuario:", linkError);
          // No interrumpimos el flujo de login si falla la vinculación
        }
      }
      
      return response;
    } catch (err) {
      setError(err.response?.data?.message || "Error al iniciar sesión");
      throw err;
    }
  }
  
  // Función para registrar un nuevo usuario
  async function handleRegister(userData) {
    try {
      setError(null);
      const response = await register(userData);
      
      // Guardar el token en localStorage
      localStorage.setItem("authToken", response.token);
      
      // Actualizar el estado del usuario
      setCurrentUser({
        username: response.username,
        roles: response.roles
      });
      
      // Si hay un carrito en localStorage, vincularlo con el usuario
      const cartId = localStorage.getItem("cartId");
      if (cartId) {
        try {
          // Vincular el carrito con el usuario autenticado
          await linkCartToUser(cartId);
          console.log("Carrito vinculado exitosamente con el usuario recién registrado", response.username);
        } catch (linkError) {
          console.error("Error al vincular el carrito con el usuario recién registrado:", linkError);
          // No interrumpimos el flujo de registro si falla la vinculación
        }
      }
      
      return response;
    } catch (err) {
      setError(err.response?.data?.message || "Error al registrar usuario");
      throw err;
    }
  }
  
  // Función para cerrar sesión
  function handleLogout() {
    localStorage.removeItem("authToken");
    setCurrentUser(null);
  }
  
  // Valor del contexto
  const value = {
    currentUser,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout,
    error,
    loading
  };
  
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
