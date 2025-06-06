import React, { useState } from "react";

// Tarjeta individual de producto
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardActions from "@mui/material/CardActions";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Snackbar from "@mui/material/Snackbar";
import { useCart } from "./CartContext";

// Tarjeta individual de producto
function ProductCard({ product }) {
  // Props: product { id, name, description, price, size, type }
  const { addToCart } = useCart();
  const [open, setOpen] = useState(false);

  const handleAdd = () => {
    // Adaptar el objeto al DTO esperado por el backend
    const cartItem = {
      productId: product.id, // Mapea 'id' a 'productId'
      productName: product.name || product.description,
      quantity: 1, // Puedes cambiar esto si hay selector de cantidad
      unitPrice: product.price,
      // Puedes añadir más campos opcionales si lo deseas
    };
    addToCart(cartItem);
    setOpen(true);
  };


  return (
    <Card sx={{ minHeight: 320, display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: "center" }}>
      {/* Imagen del producto */}
      <img
        src={product.mage ? process.env.PUBLIC_URL + "/" + (product.mage.endsWith('.svg') ? product.mage : product.mage.replace(/\.(jpg|png)$/i, '.svg')) : process.env.PUBLIC_URL + "/logo512.png"}
        alt={product.description}
        style={{ width: 160, height: 160, objectFit: "cover", marginTop: 16, borderRadius: 8, background: "#f5f5f5", border: "1px solid #ddd", display: "block" }}
        onError={e => { e.target.onerror = null; e.target.src = process.env.PUBLIC_URL + "/logo512.png"; }}
      />
      <CardContent sx={{ width: "100%" }}>
        <Typography variant="h6" gutterBottom sx={{ px: 2, textAlign: 'center', overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', minHeight: 56 }}>{product.name || product.description}</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1, px: 2, textAlign: 'center' }}>{product.description}</Typography>
        <Typography variant="subtitle2" sx={{ px: 2, textAlign: 'center' }}>Tipo: {product.type}</Typography>
        <Typography variant="subtitle2" sx={{ px: 2, textAlign: 'center' }}>Talla/Peso: {product.size}</Typography>
        <Typography variant="h6" sx={{ mt: 1, px: 2, textAlign: 'center' }}>{product.price?.toFixed(2)} €</Typography>
      </CardContent>
      <CardActions>
        <Button variant="contained" color="primary" fullWidth sx={{ borderRadius: '20px' }} onClick={handleAdd}>
          Añadir al carrito
        </Button>
        <Snackbar
          open={open}
          autoHideDuration={1500}
          onClose={() => setOpen(false)}
          message="Producto añadido a la cesta"
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        />
      </CardActions>
    </Card>
  );
}


export default ProductCard;
