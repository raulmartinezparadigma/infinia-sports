import React from "react";

// Resumen del pedido completo
import { useCart } from "./CartContext";
import { Box, Typography, Paper, Divider, List, ListItem, ListItemText, Button } from "@mui/material";
import BackgroundCarousel from './BackgroundCarousel';

function OrderSummary({ onNext, onBack }) {
  const { cart } = useCart();
  const shipping = JSON.parse(localStorage.getItem("shippingAddress") || "{}");
  const billing = JSON.parse(localStorage.getItem("billingAddress") || "{}" );
  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <Box sx={{ position: 'relative', mt: 4, mb: 4, minHeight: 500 }}>
      {/* Carrusel de fondo */}
      <BackgroundCarousel borderRadius={4} minHeight={500} />
      {/* Contenido principal del resumen */}
      <Paper elevation={4} sx={{ p: 3, maxWidth: 600, margin: '32px auto', position: 'relative', zIndex: 2, borderRadius: 4, backdropFilter: 'blur(0.5px)' }}>
        <Typography variant="subtitle1">Dirección de envío</Typography>
        <Typography variant="body2" sx={{ mb: 2 }}>
          {shipping.fullName}<br />
          {shipping.address}<br />
          {shipping.city}, {shipping.postalCode} {shipping.province}<br />
          {shipping.country}<br />
          {shipping.phone}
        </Typography>
        <Typography variant="subtitle1">Dirección de facturación</Typography>
        <Typography variant="body2" sx={{ mb: 2 }}>
          {billing.fullName}<br />
          {billing.address}<br />
          {billing.city}, {billing.postalCode} {billing.province}<br />
          {billing.country}<br />
          NIF/CIF: {billing.nif}
        </Typography>
        <Divider sx={{ my: 2 }} />
        <Typography variant="subtitle1">Productos</Typography>
        <List dense>
          {cart.map(item => (
            <ListItem key={item.id} sx={{ pl: 0 }}>
              <ListItemText
                primary={`${item.name || item.description} x${item.quantity}`}
                secondary={`${(item.price * item.quantity).toFixed(2)} €`}
              />
            </ListItem>
          ))}
        </List>
        <Divider sx={{ my: 2 }} />
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Total:</Typography>
          <Typography variant="h6">{total.toFixed(2)} €</Typography>
        </Box>
        <Box display="flex" justifyContent="flex-end" gap={2} mt={3}>
          {typeof onBack === 'function' && (
            <Button variant="contained" color="secondary" size="large" startIcon={<span style={{fontSize:20}}>&larr;</span>} onClick={onBack}>
              Volver
            </Button>
          )}
          <Button variant="contained" color="primary" size="large" onClick={typeof onNext === 'function' ? onNext : undefined}>
            Ir a pago
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default OrderSummary;
