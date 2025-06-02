import React from "react";

// Página del carrito de compras
import CartView from "../components/CartView";
import CartSummary from "../components/CartSummary";
import ProductList from "../components/ProductList";

function Cart() {
  // Vista del carrito
  return (
    <div>
      <CartView />
      <CartSummary />
      <div style={{ maxWidth: 1100, margin: '0 auto', marginTop: 32 }}>
        <h3 style={{ marginBottom: 16 }}>¿Te puede interesar...?</h3>
        <ProductList related={true} />
      </div>
    </div>
  );
}

export default Cart;
