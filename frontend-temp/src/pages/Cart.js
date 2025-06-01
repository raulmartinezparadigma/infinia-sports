import React from "react";

// Página del carrito de compras
import CartView from "../components/CartView";
import CartSummary from "../components/CartSummary";

function Cart() {
  // Vista del carrito
  return (
    <div>
      <h2>Cart Page</h2>
      <CartView />
      <CartSummary />
    </div>
  );
}

export default Cart;
