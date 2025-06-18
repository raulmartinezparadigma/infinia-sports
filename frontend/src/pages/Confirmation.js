import React from "react";

// Página de confirmación de pedido
import PaymentConfirmation from "../components/PaymentConfirmation";

function Confirmation({ paymentMethod, isAnonymous = false }) {
  // Confirmación de pedido completado
  return (
    <div>
      <PaymentConfirmation paymentMethod={paymentMethod} isAnonymous={isAnonymous} />
    </div>
  );
}

export default Confirmation;
