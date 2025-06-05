import React from "react";
import { useCart } from "./CartContext";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton, Typography, Button, Box
} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';

import BackgroundCarousel from './BackgroundCarousel';

function CartView() {
  const { cart, updateQuantity, removeFromCart } = useCart();

  if (cart.length === 0) {
    return <Typography variant="h6" sx={{ mt: 4, textAlign: 'center' }}>El carrito está vacío.</Typography>;
  }

  // Depuración: mostrar estructura real del carrito
  console.log("CART DEBUG:", cart);

  return (
    <>
      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Producto</TableCell>
              <TableCell align="right">Precio</TableCell>
              <TableCell align="center">Cantidad</TableCell>
              <TableCell align="right">Subtotal</TableCell>
              <TableCell align="center">Eliminar</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {cart.map((item) => (
              <TableRow key={item.id}>
                <TableCell>{item.name || item.description}</TableCell>
                <TableCell align="right">{item.price?.toFixed(2)} €</TableCell>
                <TableCell align="center">
                  <Button size="small" onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}>-</Button>
                  <span style={{ margin: '0 8px' }}>{item.quantity}</span>
                  <Button size="small" onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</Button>
                </TableCell>
                <TableCell align="right">{(item.price * item.quantity).toFixed(2)} €</TableCell>
                <TableCell align="center">
                  <IconButton color="error" onClick={() => removeFromCart(item.id)}>
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {/* Banner/carrusel */}
      <Box sx={{ position: 'relative', width: '100%', height: 220, my: 2, overflow: 'hidden', borderRadius: 4 }}>
        <BackgroundCarousel borderRadius={4} minHeight={220} />
        {/* Capa de difuminado extra */}
        <Box sx={{
          position: 'absolute',
          inset: 0,
          zIndex: 2,
          backdropFilter: 'blur(3px)',
          pointerEvents: 'none',
          borderRadius: 4
        }} />
      </Box>
      {/* Aquí iría la sección 'Te puede interesar' */}
    </>
  );
}

export default CartView;
