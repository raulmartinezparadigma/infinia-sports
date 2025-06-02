import React from "react";

// Página de proceso de checkout
import CheckoutStepper from "../components/CheckoutStepper";
import ShippingForm from "../components/ShippingForm";
import BillingForm from "../components/BillingForm";
import OrderSummary from "../components/OrderSummary";
import PaymentSelector from "../components/PaymentSelector";
import PaymentSimulator from "../components/PaymentSimulator";
import Confirmation from "./Confirmation";

import { useState } from "react";
import MiniCart from "../components/MiniCart";

function Checkout() {
  // Estado del paso actual
  const [step, setStep] = useState(0);

  return (
    <div>
      {step < 5 && <MiniCart position="top" />}
      {step === 0 && <ShippingForm onNext={() => setStep(1)} />}
      {step === 1 && <BillingForm onNext={() => setStep(2)} />}
      {step === 2 && <OrderSummary onNext={() => setStep(3)} />}
      {step === 3 && <PaymentSelector onNext={() => setStep(4)} />}
      {step === 4 && <PaymentSimulator onSuccess={() => setStep(5)} />}
      {step === 5 && <Confirmation />}
    </div>
  );
}

export default Checkout;
