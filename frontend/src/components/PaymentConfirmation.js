import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Paper, Typography, Box, Button } from "@mui/material";
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PaymentIcon from '@mui/icons-material/Payment';


// Confirmación de pago exitoso

function PaymentConfirmation({ paymentMethod: paymentMethodProp }) {
  const location = useLocation();
  const navigate = useNavigate();
  // Detecta el método de pago desde el estado de navegación (location.state) o desde la prop
  const paymentMethod = paymentMethodProp || location.state?.paymentMethod;

  return (
    <Paper elevation={3} sx={{ maxWidth: 480, mx: 'auto', mt: 8, p: 4, textAlign: 'center' }}>
      <CheckCircleOutlineIcon color="success" sx={{ fontSize: 64, mb: 2 }} />
      <Typography variant="h5" gutterBottom>¡Gracias por tu compra!</Typography>
      {paymentMethod === "redsys" ? (
        <>
          <PaymentIcon color="success" sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="body1" sx={{ mb: 2 }}>
            Tu pago con <b>tarjeta Redsys</b> se ha procesado correctamente.<br />
            Recibirás un correo con la confirmación y los detalles de tu pedido.
          </Typography>
        </>
      ) : (
        <Typography variant="body1" sx={{ mb: 2 }}>
          Tu pedido ha sido confirmado y recibirás un correo con los detalles en breve.<br />
          Si tienes cualquier duda, contacta con nuestro equipo de soporte.
        </Typography>
      )}
      <Box display="flex" justifyContent="center" alignItems="center" gap={1} sx={{ mb: 2 }}>
        <ShoppingCartIcon color="primary" />
        <Typography variant="subtitle1">Infinia Sports</Typography>
      </Box>
      <Button variant="contained" color="primary" onClick={() => navigate("/")}>Volver a la tienda</Button>
    </Paper>
  );
}

export default PaymentConfirmation;
