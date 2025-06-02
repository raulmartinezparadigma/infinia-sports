import React from "react";

// Página de selección y procesamiento de pago
import PaymentSelector from "../components/PaymentSelector";
import BizumPayment from "../components/BizumPayment";
import RedsysPayment from "../components/RedsysPayment";
import BankTransferPayment from "../components/BankTransferPayment";
import PaymentConfirmation from "../components/PaymentConfirmation";

function Payment() {
  // Selección y procesamiento de método de pago
  return (
    <div>
      <h2>Payment Page</h2>
      <PaymentSelector />
      <BizumPayment />
      <RedsysPayment />
      <BankTransferPayment />
      <PaymentConfirmation />
    </div>
  );
}

export default Payment;
