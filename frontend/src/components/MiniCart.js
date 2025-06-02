import React from "react";
import { useCart } from "./CartContext";
import { Box, Paper, Typography, Divider, List, ListItem, ListItemText, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

// Minicesta fija visible en todas las páginas
function MiniCart() {
  const { cart } = useCart();
  const navigate = useNavigate();
  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <Paper elevation={4} sx={{
      position: 'fixed',
      right: 24,
      top: 120,
      width: 320,
      zIndex: 1300,
      p: 2,
      borderRadius: 3,
      boxShadow: 6,
      backgroundColor: 'white',
      maxHeight: '60vh',
      overflowY: 'auto'
    }}>
      <Typography variant="subtitle1" align="center" gutterBottom>Resumen de la cesta</Typography>
      <Divider sx={{ mb: 1 }} />
      {cart.length === 0 ? (
        <Typography variant="body2" align="center">Tu cesta está vacía</Typography>
      ) : (
        <>
          <List dense>
            {cart.map(item => (
              <ListItem key={item.id} sx={{ pl: 0 }}>
                <ListItemText
                  primary={`${item.name || item.description} x${item.quantity}`}
                  secondary={`${item.price.toFixed(2)} € c/u`}
                />
              </ListItem>
            ))}
          </List>
          <Divider sx={{ my: 1 }} />
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="body1">Total:</Typography>
            <Typography variant="h6">{total.toFixed(2)} €</Typography>
          </Box>
          <Button
            variant="contained"
            color="primary"
            fullWidth
            sx={{ mt: 2 }}
            onClick={() => navigate('/cart')}
          >
            Ver carrito
          </Button>
        </>
      )}
    </Paper>
  );
}

export default MiniCart;
