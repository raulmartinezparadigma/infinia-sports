import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// API para pago por transferencia bancaria
export async function payByTransfer({ orderId, amount, titular }) {
  const response = await axios.post(`${API_BASE}/api/payments/transfer`, {
    orderId,
    amount,
    titular
  });
  return response.data;
}
