import React, { useState } from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Snackbar from "@mui/material/Snackbar";
import { useCart } from "./CartContext";

function ProductCard({ product }) {
  const { addToCart } = useCart();
  const [open, setOpen] = useState(false);
  const [hover, setHover] = useState(false);

  const handleAdd = () => {
    const price = typeof product.price === 'number' ? product.price :
      (typeof product.price === 'string' ? parseFloat(product.price) : 0);
    if (price === 0) {
      console.warn(`Producto con ID ${product.id} ('${product.name}') no tiene un precio válido. Usando 0.`);
    }
    const cartItem = {
      id: product.id,
      productId: product.id,
      productName: product.description || 'Producto',
      description: product.description || '',
      quantity: 1,
      unitPrice: price,
      price: price,
      totalPrice: price,
      productImageUrl: product.imageUrl,
    };
    addToCart(cartItem);
    setOpen(true);
  };

  return (
    <Card
      sx={{
        minHeight: 420,
        height: '100%',
        minWidth: 320,
        maxWidth: 1,
        width: '100%',
        boxShadow: 'none',
        borderRadius: 3,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'stretch',
        justifyContent: 'flex-start',
        background: '#fff',
        p: 0,
        transition: 'box-shadow 0.2s',
        position: 'relative',
        margin: 0,
        '&:hover': { boxShadow: '0 4px 24px #b3c6ff33' }
      }}
    >
      {/* Imagen grande */}
      <div style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 220, marginTop: 16, marginBottom: 10 }}>
        <img
          src={product.imageUrl ? `${process.env.PUBLIC_URL}/${product.imageUrl}` : `${process.env.PUBLIC_URL}/logo512.png`}
          alt={product.name || product.description}
          style={{ height: 200, width: '90%', objectFit: "contain", borderRadius: 8, background: '#fafafa' }}
          onError={e => { e.target.onerror = null; e.target.src = process.env.PUBLIC_URL + "/logo512.png"; }}
        />
      </div>
      <CardContent sx={{
        width: "100%",
        textAlign: 'center',
        p: 0,
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
        alignItems: 'center',
        justifyContent: 'space-between',
        minHeight: 0,
        height: '100%'
      }}>
        <Typography variant="subtitle1" sx={{ fontWeight: 700, minHeight: 48, fontSize: 19, mb: 1, color: '#212121', overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', textTransform: 'uppercase' }}>
          {product.name || product.description}
        </Typography>
        {product.oldPrice && (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 8 }}>
            <Typography variant="h6" sx={{ color: 'red', fontWeight: 900, fontSize: 18, textDecoration: 'line-through', marginRight: 8 }}>
              {product.oldPrice?.toFixed(2)} €
            </Typography>
            <Typography variant="h6" sx={{ color: 'blue', fontWeight: 900, fontSize: 22 }}>
              {product.price?.toFixed(2)} €
            </Typography>
            <Typography variant="body2" sx={{ color: 'green', fontWeight: 700, fontSize: 14, marginLeft: 8 }}>
              Promoción
            </Typography>
          </div>
        )}
        {!product.oldPrice && (
          <Typography variant="h6" sx={{ color: '#1976d2', fontWeight: 900, fontSize: 26, mb: 1 }}>
            {product.price?.toFixed(2)} €
          </Typography>
        )}
        <Button
          variant="contained"
          color="primary"
          sx={{ borderRadius: '16px', fontWeight: 700, fontSize: 15, px: 2, py: 0.5, mt: 'auto', mb: 0, minHeight: 32, minWidth: 120, alignSelf: 'center', boxShadow: 'none' }}
          onClick={handleAdd}
        >
          Añadir al carrito
        </Button>
      </CardContent>
      <Snackbar
        open={open}
        autoHideDuration={1500}
        onClose={() => setOpen(false)}
        message="Producto añadido a la cesta"
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </Card>
  );
}

export default ProductCard;
