import React from "react";

// Página de proceso de checkout
import CheckoutStepper from "../components/CheckoutStepper";
import ShippingForm from "../components/ShippingForm";
import BillingForm from "../components/BillingForm";
import OrderSummary from "../components/OrderSummary";

function Checkout() {
  // Proceso de compra en pasos
  return (
    <div>
      <h2>Checkout Page</h2>
      <CheckoutStepper />
      <ShippingForm />
      <BillingForm />
      <OrderSummary />
    </div>
  );
}

export default Checkout;
