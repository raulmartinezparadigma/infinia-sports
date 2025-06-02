import React, { useState } from "react";
import { Box, Typography, Button, CircularProgress, Alert, Paper } from "@mui/material";

function PaymentSimulator({ onSuccess }) {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSimulatePayment = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setSuccess(true);
      setTimeout(() => {
        if (typeof onSuccess === "function") onSuccess();
      }, 1000);
    }, 2000);
  };

  return (
    <Paper elevation={2} sx={{ p: 4, maxWidth: 400, mx: "auto", mt: 6, textAlign: "center" }}>
      <Typography variant="h6" gutterBottom>
        Simulación de pago
      </Typography>
      <Typography variant="body1" sx={{ mb: 3 }}>
        Pulsa el botón para simular un pago exitoso y continuar con tus pruebas.
      </Typography>
      {success ? (
        <Alert severity="success" sx={{ mb: 2 }}>¡Pago simulado correctamente!</Alert>
      ) : (
        <Button
          variant="contained"
          color="primary"
          size="large"
          onClick={handleSimulatePayment}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} color="inherit" /> : "Simular pago"}
        </Button>
      )}
    </Paper>
  );
}

export default PaymentSimulator;
