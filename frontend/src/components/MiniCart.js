import React from "react";
import { useCart } from "./CartContext";
import { Box, Paper, Typography, Divider, List, ListItem, ListItemText, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

// Minicesta fija visible en todas las páginas
// Permite personalizar la posición top por prop
function MiniCart({ top = 120 }) {
  const { cart } = useCart();
  const navigate = useNavigate();

  // Si el carrito no está cargado o no tiene items, no mostrar nada o un estado de carga
  if (!cart || !cart.items) {
    return null;
  }

  return (
    <Paper elevation={4} sx={{
      position: 'absolute',
      right: 24,
      top: 242,
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
      {cart.items.length === 0 ? (
        <Typography variant="body2" align="center">Tu cesta está vacía</Typography>
      ) : (
        <>
          <List dense>
            {cart.items.map(item => (
              <ListItem key={item.id} sx={{ pl: 0 }}>
                <Box sx={{ mr: 1 }}>
                  <img
                    src={item?.productImageUrl ? `${process.env.PUBLIC_URL}/${item.productImageUrl}` : `${process.env.PUBLIC_URL}/logo512.png`}
                    alt={item?.name || item?.description || 'Producto'}
                    style={{ width: 40, height: 40, objectFit: 'cover', borderRadius: 4 }}
                    onError={e => { e.target.onerror = null; e.target.src = process.env.PUBLIC_URL + "/logo512.png"; }}
                  />
                </Box>
                <ListItemText
                  primary={`${item.name || item.description} x${item.quantity}`}
                  secondary={`${item.unitPrice.toFixed(2)} € c/u`}
                />
              </ListItem>
            ))}
          </List>
          <Divider sx={{ my: 1 }} />
          <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
            <Typography variant="body2">Subtotal:</Typography>
            <Typography variant="body2">{cart.subtotal.toFixed(2)} €</Typography>
          </Box>
          <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
            <Typography variant="body2">Gastos de envío:</Typography>
            <Typography variant="body2">{cart.shippingCost.toFixed(2)} €</Typography>
          </Box>
          <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
            <Typography variant="body2">IVA (21%):</Typography>
            <Typography variant="body2">{cart.tax.toFixed(2)} €</Typography>
          </Box>
          <Divider sx={{ my: 1 }} />
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="body1">Total:</Typography>
            <Typography variant="h6">{cart.total.toFixed(2)} €</Typography>
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
