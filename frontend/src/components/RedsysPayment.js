import React, { useState } from "react";
import axios from 'axios'; // Importar axios
import { useCart } from "./CartContext";
import { useNavigate } from "react-router-dom";
import { 
  TextField, 
  Button, 
  Box, 
  Typography, 
  CircularProgress, 
  Paper 
} from "@mui/material";

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080'; // Definir API_BASE

// Formulario y lógica de pago Redsys
function RedsysPayment({ onSuccess }) {
  const { cartId, cart, clearCartAndReload } = useCart();
  const [form, setForm] = useState({
    cardNumber: "",
    cardHolder: "",
    expiryDate: "",
    cvv: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  // Calcula el importe total del carrito
  const amount = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // Maneja cambios en los campos del formulario
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Lógica para llamar al backend Redsys
  const handlePay = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess(false);

    try {
      const response = await axios.post(`${API_BASE}/api/payments/redsys`, {
        orderId: cartId,
        cardNumber: form.cardNumber,
        cardHolder: form.cardHolder,
        expiryDate: form.expiryDate,
        cvv: form.cvv,
        amount,
      });

      // axios lanza errores para respuestas no 2xx automáticamente
      // la propiedad data ya contiene el JSON
      if (response.data && response.data.status === "COMPLETED") {
        // Sincroniza el carrito tras el pago exitoso
        await clearCartAndReload();
        setSuccess(true);
        // Llama a onSuccess para que el flujo de navegación lo controle PaymentSelector
        setTimeout(() => {
          if (typeof onSuccess === "function") onSuccess();
        }, 1000);
      } else {
        setError("El pago no se completó correctamente.");
      }
    } catch (err) {
      setError("Error al procesar el pago. Inténtalo de nuevo.");
    }
    setLoading(false);
  };

  return (
    <Paper sx={{ p: 3, maxWidth: 400, mx: "auto" }}>
      <Typography variant="h6" gutterBottom>
        Pago con tarjeta (Redsys)
      </Typography>
      <form onSubmit={handlePay}>
        <TextField
          label="Número de tarjeta"
          name="cardNumber"
          value={form.cardNumber}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Titular"
          name="cardHolder"
          value={form.cardHolder}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Caducidad (MM/AA)"
          name="expiryDate"
          value={form.expiryDate}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="CVV"
          name="cvv"
          value={form.cvv}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <Box mt={2} display="flex" justifyContent="center">
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={loading}
            fullWidth
          >
            {loading ? <CircularProgress size={24} /> : "Pagar"}
          </Button>
        </Box>
      </form>
      {error && (
        <Typography color="error" sx={{ mt: 2 }}>
          {error}
        </Typography>
      )}
      {success && (
        <Typography color="success.main" sx={{ mt: 2 }}>
          ¡Pago realizado correctamente!
        </Typography>
      )}
    </Paper>
  );
}

export default RedsysPayment;
