import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

// Importación de páginas principales
import Catalog from "./pages/Catalog";
import ProductDetail from "./pages/ProductDetail";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Payment from "./pages/Payment";
import Confirmation from "./pages/Confirmation";
import OrderHistory from "./pages/OrderHistory";
import OrderDetail from "./pages/OrderDetail";

// Componentes de autenticación
import Login from "./components/Login";
import Register from "./components/Register";

// Componente principal de la app
import Navbar from "./components/Navbar";

// Contextos
import { CartProvider } from "./components/CartContext";
import { AuthProvider } from "./components/AuthContext";

// Componente para rutas protegidas que requieren autenticación
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("authToken");
  if (!token) {
    // Redirigir a login si no hay token
    return <Navigate to="/login" replace />;
  }
  return children;
};

// Componente para checkout que permite tanto usuarios autenticados como anónimos
const CheckoutRoute = ({ children }) => {
  // No requiere autenticación, permite checkout anónimo
  return children;
};

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <Navbar />
          {/* Definición de rutas principales */}
          <Routes>
            <Route path="/" element={<Catalog />} />
            <Route path="/catalog" element={<Catalog />} />
            <Route path="/product/:id" element={<ProductDetail />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/checkout" element={
              <CheckoutRoute>
                <Checkout />
              </CheckoutRoute>
            } />
            <Route path="/payment" element={
              <CheckoutRoute>
                <Payment />
              </CheckoutRoute>
            } />
            <Route path="/confirmation" element={
              <CheckoutRoute>
                <Confirmation />
              </CheckoutRoute>
            } />
            <Route path="/pedidos" element={
              <ProtectedRoute>
                <OrderHistory />
              </ProtectedRoute>
            } />
            <Route path="/pedidos/:orderId" element={
              <ProtectedRoute>
                <OrderDetail />
              </ProtectedRoute>
            } />
            <Route path="/profile" element={
              <ProtectedRoute>
                <div>Mi Perfil</div>
              </ProtectedRoute>
            } />
          </Routes>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;
