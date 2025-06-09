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
import { useCart } from "../components/CartContext";

function Checkout() {
  const navigate = useNavigate();
  const { cart } = useCart();
  // Estado del paso actual
  const [step, setStep] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState(null);

  // Generar orderId temporal (en producción vendría del backend)
  const orderId = React.useMemo(() => {
    return 'ORDER-' + Math.random().toString(36).substring(2, 12).toUpperCase();
  }, []);
  const amount = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <div>
      {step < 5 && <MiniCart position="top" />}
      {step === 0 && <ShippingForm onNext={() => setStep(1)} onBack={() => navigate('/cart')} />}
      {step === 1 && <BillingForm onNext={() => setStep(2)} onBack={() => setStep(0)} />}
      {step === 2 && <OrderSummary onNext={() => setStep(3)} onBack={() => setStep(1)} />}

      {step === 3 && <PaymentSelector 
        orderId={orderId}
        amount={amount}
        onNext={(args) => {
          if (args && args.paymentMethod) {
            setPaymentMethod(args.paymentMethod);
            setStep(5);
          } else {
            setStep(4);
          }
        }} 
        onBack={() => setStep(1)} 
      />}
      {step === 4 && <PaymentSimulator onSuccess={() => setStep(5)} onBack={() => setStep(3)} />}
      {step === 5 && <Confirmation paymentMethod={paymentMethod} />}
    </div>
  );
}

export default Checkout;
