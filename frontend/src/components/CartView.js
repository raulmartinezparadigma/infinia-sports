import React from "react";
import { useCart } from "./CartContext";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton, Typography, Button
} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';

function CartView() {
  const { cart, updateQuantity, removeFromCart } = useCart();

  if (cart.length === 0) {
    return <Typography variant="h6" sx={{ mt: 4, textAlign: 'center' }}>El carrito está vacío.</Typography>;
  }

  return (
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
  );
}

export default CartView;
