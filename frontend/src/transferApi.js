// API para pago por transferencia bancaria
export async function payByTransfer({ orderId, amount, titular }) {
  const response = await fetch("/payments/transfer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ orderId, amount, titular })
  });
  if (!response.ok) {
    throw new Error("Error al registrar el pago por transferencia");
  }
  return await response.json();
}
