import React from "react";
import { useCart } from "./CartContext";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton, Typography, Button, Box
} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';

import BackgroundCarousel from './BackgroundCarousel';
import { useNavigate } from 'react-router-dom';

function CartView() {
  const { cart, updateQuantity, removeFromCart } = useCart();
  const navigate = useNavigate();

  if (cart.length === 0) {
    // Mostrar logo grande de Infinia Sports si el carrito está vacío
    // Mostrar logo grande de Infinia Sports a la izquierda si el carrito está vacío
    // Mostrar logo grande de Infinia Sports centrado en el rectángulo si el carrito está vacío
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: 36 }}>
        <div style={{ width: '100%', display: 'flex', justifyContent: 'center', marginBottom: 18 }}>
          <img src={process.env.PUBLIC_URL + '/infinia_sports.jpg'} alt="Infinia Sports logo" style={{ maxHeight: 240, maxWidth: 400, objectFit: 'contain', display: 'block' }} />
        </div>
        <Typography variant="h6" sx={{ textAlign: 'center' }}>El carrito está vacío.</Typography>
      </div>
    );
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
                <TableCell>
                  <div>
                    <Typography variant="subtitle1">{item.name || 'Producto'}</Typography>
                    {item.description && (
                      <Typography variant="body2" color="text.secondary">{item.description}</Typography>
                    )}
                  </div>
                </TableCell>
                <TableCell align="right">{(item.price !== undefined ? item.price : 0).toFixed(2)} €</TableCell>
                <TableCell align="center">
                  <Button size="small" onClick={() => {
                    const newQuantity = item.quantity - 1;
                    if (newQuantity <= 0) {
                      removeFromCart(item.id);
                    } else {
                      updateQuantity(item.id, newQuantity);
                    }
                  }}>-</Button>
                  <span style={{ margin: '0 8px' }}>{item.quantity || 1}</span>
                  <Button size="small" onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</Button>
                </TableCell>
                <TableCell align="right">{(item.totalPrice !== undefined ? item.totalPrice : 0).toFixed(2)} €</TableCell>
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
      {/* Botón para volver al catálogo */}
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <Button variant="outlined" color="primary" onClick={() => navigate('/')}>Volver al catálogo</Button>
      </Box>
      {/* Aquí iría la sección 'Te puede interesar' */}
    </>
  );
}

export default CartView;
