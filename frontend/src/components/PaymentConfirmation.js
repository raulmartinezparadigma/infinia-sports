import React from "react";

// Confirmación de pago exitoso
import { Box, Typography, Button, Paper } from "@mui/material";
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import { useNavigate } from "react-router-dom";

function PaymentConfirmation() {
  const navigate = useNavigate();
  return (
    <Paper elevation={3} sx={{ maxWidth: 480, mx: 'auto', mt: 8, p: 4, textAlign: 'center' }}>
      <CheckCircleOutlineIcon color="success" sx={{ fontSize: 64, mb: 2 }} />
      <Typography variant="h5" gutterBottom>¡Gracias por tu compra!</Typography>
      <Typography variant="body1" sx={{ mb: 2 }}>
        Tu pedido ha sido confirmado y recibirás un correo con los detalles en breve.<br />
        Si tienes cualquier duda, contacta con nuestro equipo de soporte.
      </Typography>
      <Box display="flex" justifyContent="center" alignItems="center" gap={1} sx={{ mb: 2 }}>
        <ShoppingCartIcon color="primary" />
        <Typography variant="subtitle1">Infinia Sports</Typography>
      </Box>
      <Button variant="contained" color="primary" onClick={() => navigate("/")}>Volver a la tienda</Button>
    </Paper>
  );
}

export default PaymentConfirmation;
