import React from "react";

// Tarjeta individual de producto
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardActions from "@mui/material/CardActions";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";

// Tarjeta individual de producto
function ProductCard({ product }) {
  // Props: product { id, name, description, price, size, type }
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
        <Button variant="contained" color="primary" fullWidth>Añadir al carrito</Button>
      </CardActions>
    </Card>
  );
}


export default ProductCard;
