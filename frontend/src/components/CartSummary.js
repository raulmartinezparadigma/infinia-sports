import React from "react";
import { useCart } from "./CartContext";
import { Box, Typography, Button, Paper, Divider } from "@mui/material";
import { useNavigate } from "react-router-dom";

function CartSummary() {
  const { cart, clearCart } = useCart();
  const navigate = useNavigate();

  // Comprobar si el carrito o sus items están disponibles
  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <Paper elevation={2} sx={{ mt: 3, p: 3, maxWidth: 400, margin: '32px auto' }}>
        <Typography variant="h6" gutterBottom>Resumen del carrito</Typography>
        <Typography>El carrito está vacío.</Typography>
      </Paper>
    );
  }

  // Extraer valores del objeto cart con valores por defecto
  const {
    subtotal = 0,
    shippingCost = 0,
    tax = 0,
    total = 0,
    items = []
  } = cart;

  const totalItems = items.reduce((sum, item) => sum + (item.quantity || 0), 0);

  return (
    <Paper elevation={2} sx={{ mt: 3, p: 3, maxWidth: 400, margin: '32px auto' }}>
      <Typography variant="h6" gutterBottom>Resumen del carrito</Typography>

      <Box display="flex" justifyContent="space-between" my={1}>
        <Typography>Subtotal ({totalItems} productos):</Typography>
        <Typography>{subtotal.toFixed(2)} €</Typography>
      </Box>

      <Box display="flex" justifyContent="space-between" my={1}>
        <Typography>Gastos de envío:</Typography>
        <Typography>{shippingCost.toFixed(2)} €</Typography>
      </Box>

      <Box display="flex" justifyContent="space-between" my={1}>
        <Typography>Impuestos (IVA):</Typography>
        <Typography>{tax.toFixed(2)} €</Typography>
      </Box>

      <Divider sx={{ my: 2 }} />

      <Box display="flex" justifyContent="space-between" my={1}>
        <Typography variant="h6">Total:</Typography>
        <Typography variant="h6">{total.toFixed(2)} €</Typography>
      </Box>

      <Button
        variant="contained"
        color="primary"
        fullWidth
        sx={{ mt: 2 }}
        disabled={items.length === 0}
        onClick={() => navigate("/checkout")}
      >
        Ir a Checkout
      </Button>

      <Button
        variant="outlined"
        color="secondary"
        fullWidth
        sx={{ mt: 2 }}
        disabled={items.length === 0}
        onClick={clearCart}
      >
        Vaciar carrito
      </Button>
    </Paper>
  );
}

export default CartSummary;
