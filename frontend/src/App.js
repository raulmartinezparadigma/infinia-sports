import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// Importación de páginas principales (aún vacías)

import Catalog from "./pages/Catalog";
import ProductDetail from "./pages/ProductDetail";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Payment from "./pages/Payment";
import Confirmation from "./pages/Confirmation";

// Componente principal de la app
import Navbar from "./components/Navbar";

import { CartProvider } from "./components/CartContext";


function App() {
  return (
    <CartProvider>
      <Router>
        <Navbar />
        {/* Definición de rutas principales */}
        <Routes>
          <Route path="/" element={<Catalog />} />
          <Route path="/catalog" element={<Catalog />} />
          <Route path="/product/:id" element={<ProductDetail />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/payment" element={<Payment />} />
          <Route path="/confirmation" element={<Confirmation />} />
        </Routes>
      </Router>
    </CartProvider>
  );
}

export default App;
