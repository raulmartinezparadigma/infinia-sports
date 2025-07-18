import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, Button, Container, Grid, CircularProgress, Alert } from '@mui/material';
import { useCart } from './CartContext';

// Vista detallada de un producto
function ProductDetailComponent() {
  const { id } = useParams();
  const { addToCart } = useCart();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`/api/products/${id}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Producto no encontrado');
        }
        return response.json();
      })
      .then(data => {
        setProduct(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Error al cargar el producto:", err);
        setError(err.message);
        setLoading(false);
      });
  }, [id]);

  const handleAdd = () => {
    if (!product) return;
    const cartItem = {
      id: product.id,
      productId: product.id,
      productName: product.name || 'Producto',
      description: product.description || '',
      quantity: 1,
      unitPrice: product.price,
      price: product.price,
      totalPrice: product.price,
      productImageUrl: product.imageUrl,
    };
    addToCart(cartItem);
    // Opcional: mostrar una notificación de que se añadió al carrito
  };

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Container sx={{ mt: 4 }}><Alert severity="error">{error}</Alert></Container>;
  }

  if (!product) {
    return <Container sx={{ mt: 4 }}><Typography>Producto no disponible.</Typography></Container>;
  }

  return (
    <Container maxWidth={false} sx={{ mt: 5, px: { xs: 2, sm: 4 } }}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={6}>
          <Box sx={{ background: '#fafafa', borderRadius: 2, p: 2 }}>
            <img 
              src={product.imageUrl ? `${process.env.PUBLIC_URL}/${product.imageUrl}` : `${process.env.PUBLIC_URL}/logo512.png`}
              alt={product.name}
              style={{ width: '100%', height: 'auto', objectFit: 'contain' }}
            />
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
            {product.name}
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            {product.productTypeDisplayName}
          </Typography>
          <Typography variant="body1" paragraph>
            {product.description}
          </Typography>
          <Typography variant="h5" sx={{ my: 2, fontWeight: 'bold', color: 'primary.main' }}>
            {product.price?.toFixed(2)} €
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Talla/Peso: {product.size}
          </Typography>
          <Box sx={{ mt: 3 }}>
            <Button variant="contained" color="primary" size="large" onClick={handleAdd}>
              Añadir al carrito
            </Button>
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
}

export default ProductDetailComponent;
