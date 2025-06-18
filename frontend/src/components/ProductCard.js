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
  // Props: product { id, name, description, price, size, type, imageUrl }
  const { addToCart } = useCart();
  const [open, setOpen] = useState(false);

  const handleAdd = () => {
    // Asegurarse de que el precio sea un número válido
    const price = typeof product.price === 'number' ? product.price : 
                 (typeof product.price === 'string' ? parseFloat(product.price) : 0);
                 
    if (price === 0) {
      console.warn(`Producto con ID ${product.id} ('${product.name}') no tiene un precio válido. Usando 0.`);
    }
    
    // Crear un objeto con toda la información necesaria
    const cartItem = {
      id: product.id,  // Usar el ID del producto como ID del item
      productId: product.id,
      productName: product.name || 'Producto',
      description: product.description || '',  // Incluir la descripción
      quantity: 1,
      unitPrice: price,
      price: price,  // Añadir price explícitamente
      totalPrice: price, // Añadir totalPrice explícitamente
      productImageUrl: product.imageUrl,
      // Incluir otros campos del producto que puedan ser útiles
      size: product.size,
      type: product.type
    };
    
    console.log('Añadiendo al carrito:', cartItem);
    addToCart(cartItem);
    setOpen(true);
  };


  return (
    <Card sx={{ minHeight: 320, display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: "center" }}>
      {/* Imagen del producto */}
      <img
        src={product.imageUrl ? `${process.env.PUBLIC_URL}/${product.imageUrl}` : `${process.env.PUBLIC_URL}/logo512.png`}
        alt={product.name || product.description}
        style={{ height: 160, width: 160, objectFit: "contain", marginTop: 16, borderRadius: 8 }}
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
