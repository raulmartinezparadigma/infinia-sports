import React, { useState, useEffect } from "react";
import { useCart } from "./CartContext";
import { getCart } from "../cartApi";
import { Box, Typography, Button, CircularProgress, Alert, Paper, TextField } from "@mui/material";
import { processBizumPayment, confirmOrder } from "../cartApi";

// Componente que simula un pago real usando el servicio Bizum del backend
function PaymentSimulator({ onSuccess, onBack }) {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(null);
  const [phoneNumber, setPhoneNumber] = useState("");
  const [paymentId, setPaymentId] = useState(() => Math.random().toString(36).substring(2, 12)); // ID mock aleatorio
  const { userId, clearCartAndReload, cartId } = useCart();
  const [cart, setCart] = useState([]);

  // Llama al backend para confirmar el pedido y procesar el pago Bizum
  const handleRealBizumPayment = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    if (!cartId) {
      setError("No se ha podido obtener el identificador de la orden. Intenta refrescar la página o volver a crear el pedido.");
      setLoading(false);
      return;
    }
    try {
      // Obtener datos de dirección almacenados en localStorage
      const shippingAddress = JSON.parse(localStorage.getItem("shippingAddress") || "{}");
      const billingAddress = JSON.parse(localStorage.getItem("billingAddress") || "{}");
      
      // Preparar el DTO para confirmar el pedido
      const checkoutData = {
        cartId: cartId,
        email: shippingAddress.email, // Aseguramos que el email se envía al backend
        shippingAddress: {
          firstName: shippingAddress.fullName ? shippingAddress.fullName.split(' ')[0] : '',
          lastName: shippingAddress.fullName ? shippingAddress.fullName.split(' ').slice(1).join(' ') : '',
          addressLine1: shippingAddress.address,
          addressLine2: shippingAddress.addressLine2 || '',
          city: shippingAddress.city,
          state: shippingAddress.province || '',
          postalCode: shippingAddress.postalCode,
          country: shippingAddress.country,
          phoneNumber: shippingAddress.phone,
          email: shippingAddress.email
        },
        billingAddress: billingAddress.sameAsShipping ? null : {
          firstName: billingAddress.fullName ? billingAddress.fullName.split(' ')[0] : '',
          lastName: billingAddress.fullName ? billingAddress.fullName.split(' ').slice(1).join(' ') : '',
          addressLine1: billingAddress.address,
          addressLine2: billingAddress.addressLine2 || '',
          city: billingAddress.city,
          state: billingAddress.province || '',
          postalCode: billingAddress.postalCode,
          country: billingAddress.country,
          phoneNumber: billingAddress.phone,
          email: shippingAddress.email
        },
        sameAsBillingAddress: billingAddress.sameAsShipping || false,
        shippingMethod: "STANDARD",
        paymentMethod: "BIZUM"
      };
      
      // Confirmar el pedido
      console.log('[PaymentSimulator] Confirmando pedido con datos:', checkoutData);
      const order = await confirmOrder(checkoutData);
      console.log('[PaymentSimulator] Pedido confirmado:', order);
      
      // Procesar el pago
      const response = await processBizumPayment({ 
        paymentId, 
        orderId: order.id || order.orderId, 
        phoneNumber, 
        userId 
      });
      
      // Vacía y sincroniza el carrito con el backend tras el pago exitoso
      await clearCartAndReload(); // Esto asegura máxima sincronización UI-backend
      setLoading(false);
      setSuccess(true);
      
      // Espera 5 segundos tras mostrar el mensaje de éxito para que el usuario pueda leerlo
      setTimeout(() => {
        if (typeof onSuccess === "function") onSuccess();
      }, 5000);
    } catch (err) {
      console.error('[PaymentSimulator] Error:', err);
      setLoading(false);
      setError("Error al procesar el pedido o el pago. Inténtalo de nuevo.");
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
