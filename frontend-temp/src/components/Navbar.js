import React from "react";
import { Link } from "react-router-dom";

// Barra de navegación principal
function Navbar() {
  return (
    <nav style={{ padding: "1rem", background: "#1976d2" }}>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/catalog">Catalog</Link>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/product/1">ProductDetail</Link>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/cart">Cart</Link>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/checkout">Checkout</Link>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/payment">Payment</Link>
      <Link style={{ color: "#fff", marginRight: 16 }} to="/confirmation">Confirmation</Link>
    </nav>
  );
}

export default Navbar;
