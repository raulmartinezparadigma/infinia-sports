import React from "react";

// Resumen de totales del carrito
import { useCart } from "./CartContext";
import { Box, Typography, Button, Paper } from "@mui/material";
import { useNavigate } from "react-router-dom";

function CartSummary() {
  const { cart } = useCart();
  const navigate = useNavigate();

  // Cálculo de totales
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
  const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <Paper elevation={2} sx={{ mt: 3, p: 3, maxWidth: 400, margin: '32px auto' }}>
      <Typography variant="h6" gutterBottom>Resumen del carrito</Typography>
      <Box display="flex" justifyContent="space-between" my={1}>
        <span>Total de productos:</span>
        <span>{totalItems}</span>
      </Box>
      <Box display="flex" justifyContent="space-between" my={1}>
        <span>Total:</span>
        <span>{totalPrice.toFixed(2)} €</span>
      </Box>
      <Button
        variant="contained"
        color="primary"
        fullWidth
        sx={{ mt: 2 }}
        disabled={cart.length === 0}
        onClick={() => navigate("/checkout")}
      >
        Ir a Checkout
      </Button>
    </Paper>
  );
}

export default CartSummary;
