import React, { useState } from "react";
import { Box, Typography, Button, CircularProgress, Alert, Paper, TextField } from "@mui/material";
import { processBizumPayment } from "../cartApi";

// Componente que simula un pago real usando el servicio Bizum del backend
function PaymentSimulator({ onSuccess, onBack }) {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(null);
  const [phoneNumber, setPhoneNumber] = useState("");
  const [paymentId, setPaymentId] = useState(() => Math.random().toString(36).substring(2, 12)); // ID mock aleatorio

  // Llama al backend para procesar el pago Bizum
  const handleRealBizumPayment = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      const response = await processBizumPayment(paymentId, phoneNumber);
      setLoading(false);
      setSuccess(true);
      // Espera 5 segundos tras mostrar el mensaje de éxito para que el usuario pueda leerlo
      setTimeout(() => {
        if (typeof onSuccess === "function") onSuccess();
      }, 5000);
    } catch (err) {
      setLoading(false);
      setError("Error al procesar el pago Bizum. Inténtalo de nuevo.");
    }
  };


  return (
    <Paper elevation={2} sx={{ p: 4, maxWidth: 400, mx: "auto", mt: 6, textAlign: "center" }}>
      <Typography variant="h6" gutterBottom>
        Pago Bizum real
      </Typography>
      {success ? (
        // Solo mostrar mensaje de éxito cuando el pago ha sido procesado
        <>
          <Alert severity="success" sx={{ mb: 2 }}>¡Pago Bizum procesado correctamente!</Alert>
          {typeof onBack === 'function' && (
            <Box mt={4} display="flex" justifyContent="center">
              <Button variant="contained" color="secondary" size="large" startIcon={<span style={{fontSize:20}}>&larr;</span>} onClick={onBack}>
                Volver
              </Button>
            </Box>
          )}
        </>
      ) : (
        <>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Introduce tu número de teléfono para simular un pago Bizum real contra el backend.
          </Typography>
          <form onSubmit={e => { e.preventDefault(); handleRealBizumPayment(); }}>
            <TextField
              label="Teléfono Bizum"
              variant="outlined"
              value={phoneNumber}
              onChange={e => setPhoneNumber(e.target.value.replace(/[^0-9]/g, ''))}
              inputProps={{ maxLength: 9 }}
              sx={{ mb: 2, width: '100%' }}
              required
              error={phoneNumber.length > 0 && phoneNumber.length !== 9}
              helperText={phoneNumber.length > 0 && phoneNumber.length !== 9 ? 'Introduce un teléfono válido (9 dígitos)' : ''}
              disabled={loading}
            />
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            <Button
              variant="contained"
              color="primary"
              size="large"
              type="submit"
              disabled={loading || phoneNumber.length !== 9}
              sx={{ mt: 1 }}
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : "Pagar con Bizum"}
            </Button>
          </form>
          {typeof onBack === 'function' && (
            <Box mt={4} display="flex" justifyContent="center">
              <Button variant="contained" color="secondary" size="large" startIcon={<span style={{fontSize:20}}>&larr;</span>} onClick={onBack}>
                Volver
              </Button>
            </Box>
          )}
        </>
      )}
    </Paper>
  );
}

export default PaymentSimulator;
