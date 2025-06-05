import React from "react";

// Página de proceso de checkout

import ShippingForm from "../components/ShippingForm";
import BillingForm from "../components/BillingForm";
import OrderSummary from "../components/OrderSummary";
import PaymentSelector from "../components/PaymentSelector";
import PaymentSimulator from "../components/PaymentSimulator";
import Confirmation from "./Confirmation";

import { useState } from "react";
import MiniCart from "../components/MiniCart";
import { useNavigate } from "react-router-dom";

function Checkout() {
  const navigate = useNavigate();
  // Estado del paso actual
  const [step, setStep] = useState(0);

  return (
    <div>
      {step < 5 && <MiniCart position="top" />}
      {step === 0 && <ShippingForm onNext={() => setStep(1)} onBack={() => navigate('/cart')} />}
      {step === 1 && <BillingForm onNext={() => setStep(2)} onBack={() => setStep(0)} />}
      {step === 2 && <OrderSummary onNext={() => setStep(3)} onBack={() => setStep(1)} />}

      {step === 3 && <PaymentSelector onNext={() => setStep(4)} onBack={() => setStep(1)} />}
      {step === 4 && <PaymentSimulator onSuccess={() => setStep(5)} onBack={() => setStep(3)} />}
      {step === 5 && <Confirmation />}
    </div>
  );
}

export default Checkout;
