import React from "react";

// Página de proceso de checkout

import ShippingForm from "../components/ShippingForm";
import BillingForm from "../components/BillingForm";
import OrderSummary from "../components/OrderSummary";
import PaymentSelector from "../components/PaymentSelector";
import PaymentSimulator from "../components/PaymentSimulator";
import Confirmation from "./Confirmation";
import CheckoutOptions from "../components/CheckoutOptions";

import { useState, useEffect } from "react";
import MiniCart from "../components/MiniCart";
import { useNavigate } from "react-router-dom";
import { useCart } from "../components/CartContext";
import { useAuth } from "../components/AuthContext";
import { Button } from "@mui/material";

function Checkout() {
  const navigate = useNavigate();
  const { cart } = useCart();
  const { currentUser } = useAuth();
  
  // Estado del paso actual
  const [step, setStep] = useState(-1); // -1 representa la selección de modo de checkout
  const [paymentMethod, setPaymentMethod] = useState(null);
  const [isAnonymousCheckout, setIsAnonymousCheckout] = useState(false);

  // Generar orderId temporal (en producción vendría del backend)
  const orderId = React.useMemo(() => {
    return 'ORDER-' + Math.random().toString(36).substring(2, 12).toUpperCase();
  }, []);
  const amount = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
  
  // Si el usuario ya está autenticado, saltamos la pantalla de opciones
  useEffect(() => {
    if (currentUser && step === -1) {
      setStep(0); // Ir directamente al primer paso del checkout
    }
  }, [currentUser, step]);
  
  // Función para continuar como usuario anónimo
  const handleContinueAnonymous = () => {
    setIsAnonymousCheckout(true);
    setStep(0);
  };

  return (
    <div>
      {/* Pantalla de selección de modo de checkout (anónimo o autenticado) */}
      {step === -1 && (
        <CheckoutOptions onContinueAnonymous={handleContinueAnonymous} />
      )}
      
      {/* Mostrar MiniCart en todos los pasos excepto confirmación y selección de modo */}
      {step >= 0 && step < 5 && <MiniCart position="top" />}
      
      {/* Pasos del proceso de checkout */}
      {step === 0 && (
        <ShippingForm 
          onNext={() => setStep(1)} 
          onBack={() => navigate('/cart')} 
          isAnonymous={isAnonymousCheckout}
        />
      )}
      
      {step === 1 && (
        <BillingForm 
          onNext={() => setStep(2)} 
          onBack={() => setStep(0)} 
          isAnonymous={isAnonymousCheckout}
        />
      )}
      
      {step === 2 && (
        <OrderSummary 
          onNext={() => setStep(3)} 
          onBack={() => setStep(1)} 
          isAnonymous={isAnonymousCheckout}
        />
      )}

      {step === 3 && (
        <PaymentSelector 
          orderId={orderId}
          amount={amount}
          isAnonymous={isAnonymousCheckout}
          onNext={(args) => {
            if (args && args.paymentMethod) {
              setPaymentMethod(args.paymentMethod);
              setStep(5);
            } else {
              setStep(4);
            }
          }} 
          onBack={() => setStep(1)} 
        />
      )}
      
      {step === 4 && (
        <PaymentSimulator 
          onSuccess={() => setStep(5)} 
          onBack={() => setStep(3)} 
          isAnonymous={isAnonymousCheckout}
        />
      )}
      
      {step === 5 && (
        <Confirmation 
          paymentMethod={paymentMethod} 
          isAnonymous={isAnonymousCheckout}
        />
      )}

      {/* Botón para volver al catálogo solo si no es confirmación final ni selección de modo */}
      {step > -1 && step !== 5 && (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 32 }}>
          <Button variant="outlined" color="primary" onClick={() => navigate('/')}>Volver al catálogo</Button>
        </div>
      )}
    </div>
  );
}

export default Checkout;
