import React from "react";

// Página de confirmación de pedido
import PaymentConfirmation from "../components/PaymentConfirmation";

function Confirmation({ paymentMethod, isAnonymous = false }) {
  // Limpia datos guardados de dirección al finalizar pedido
  React.useEffect(() => {
    localStorage.removeItem('shippingAddress');
    localStorage.removeItem('billingAddress');
  }, []);
  // Confirmación de pedido completado
  return (
    <div>
      <PaymentConfirmation paymentMethod={paymentMethod} isAnonymous={isAnonymous} />
    </div>
  );
}

export default Confirmation;
