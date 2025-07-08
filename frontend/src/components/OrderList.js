import React from 'react';
import { Box, Paper, Grid, Typography, Button, Divider } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const OrderList = ({ orders }) => {
    const navigate = useNavigate();

    // Función para calcular el número total de productos en un pedido
    const calculateTotalProducts = (order) => {
        let total = 0;
        if (order.shippingGroups) {
            order.shippingGroups.forEach(group => {
                if (group.lineItems) {
                    group.lineItems.forEach(item => {
                        total += item.quantity;
                    });
                }
            });
        }
        return total;
    };

    // Función para obtener las imágenes de productos
    const getProductImages = (order) => {
        const images = [];
        if (order.shippingGroups) {
            order.shippingGroups.forEach(group => {
                if (group.lineItems) {
                    group.lineItems.forEach(item => {
                        if (item.productImageUrl) {
                            images.push({
                                url: item.productImageUrl,
                                id: item.id
                            });
                        }
                    });
                }
            });
        }
        return images;
    };

    // Función para formatear la fecha sin dependencias externas
    const formatDate = (dateString) => {
        if (!dateString) return '';
        try {
            const date = new Date(dateString);
            const options = { day: 'numeric', month: 'long', year: 'numeric' };
            return date.toLocaleDateString('es-ES', options);
        } catch (error) {
            console.error('Error al formatear la fecha:', error);
            return dateString;
        }
    };

    const handleViewOrder = (orderId) => {
        navigate(`/pedidos/${orderId}`);
    };

    if (!orders || orders.length === 0) {
        return (
            <Box mt={5} textAlign="center">
                <Typography variant="h6">No tiene pedidos realizados en Infinia Sports</Typography>
                <Typography variant="body1" color="text.secondary" mt={1}>
                    Cuando realice un pedido, aparecerá aquí.
                </Typography>
            </Box>
        );
    }

    return (
        <Box mt={3}>
            {orders.map((order, index) => {
                const totalProducts = calculateTotalProducts(order);
                const productImages = getProductImages(order);
                
                return (
                    <Paper 
                        key={order.id} 
                        data-testid={`order-card-${index + 1}`}
                        elevation={2} 
                        sx={{ 
                            mb: 3, 
                            p: 3,
                            '&:hover': {
                                boxShadow: 4
                            }
                        }}
                    >
                        <Box mb={2}>
                            <Typography variant="subtitle1" fontWeight="bold">
                                Entregado
                            </Typography>
                            <Typography variant="body2" color="text.secondary" data-testid={`order-date-${index + 1}`}>
                                Pedido entregado el {formatDate(order.submitDate)}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" data-testid={`order-id-${index + 1}`}>
                                ID: {order.id}
                            </Typography>
                        </Box>

                        <Grid container spacing={2} alignItems="center">
                            <Grid item xs={12} md={9}>
                                <Box display="flex" flexWrap="wrap" gap={1}>
                                    {productImages.slice(0, 6).map((img, index) => (
                                        <Box 
                                            key={`${img.id}-${index}`}
                                            sx={{
                                                width: 70,
                                                height: 70,
                                                display: 'flex',
                                                justifyContent: 'center',
                                                alignItems: 'center',
                                                overflow: 'hidden',
                                                border: '1px solid #eee'
                                            }}
                                        >
                                            <img 
                                                src={img.url} 
                                                alt="Producto" 
                                                style={{ 
                                                    maxWidth: '100%', 
                                                    maxHeight: '100%',
                                                    objectFit: 'contain'
                                                }} 
                                            />
                                        </Box>
                                    ))}
                                    
                                    {productImages.length > 6 && (
                                        <Box 
                                            sx={{
                                                width: 70,
                                                height: 70,
                                                display: 'flex',
                                                justifyContent: 'center',
                                                alignItems: 'center',
                                                bgcolor: 'rgba(0,0,0,0.05)',
                                                border: '1px solid #eee'
                                            }}
                                        >
                                            <Typography variant="body2">+{productImages.length - 6}</Typography>
                                        </Box>
                                    )}
                                </Box>
                                
                                <Typography variant="body2" mt={1}>
                                    {totalProducts} {totalProducts === 1 ? 'producto' : 'productos'}
                                </Typography>
                                
                                <Typography variant="body2" fontWeight="bold" mt={1}>
                                    {order.priceInfo?.total ? `${order.priceInfo.total.toFixed(2)} €` : ''}
                                </Typography>
                            </Grid>
                            
                            <Grid item xs={12} md={3} sx={{ display: 'flex', justifyContent: { xs: 'flex-start', md: 'flex-end' } }}>
                                <Button 
                                    variant="contained" 
                                    color="primary" 
                                    onClick={() => handleViewOrder(order.id)}
                                    sx={{ minWidth: 120 }}
                                    data-testid={`view-order-button-${index + 1}`}
                                >
                                    Ver detalle
                                </Button>
                            </Grid>
                        </Grid>
                        
                        <Divider sx={{ mt: 2, mb: 1 }} />
                        
                        <Grid container spacing={1}>
                            <Grid item xs={12}>
                                <Box display="flex" alignItems="center" gap={2}>
                                    <Typography variant="body2" color="text.secondary">
                                        Tienda
                                    </Typography>
                                    <Typography variant="body2" fontWeight="bold">
                                        Infinia Sports
                                    </Typography>
                                </Box>
                            </Grid>
                        </Grid>
                    </Paper>
                );
            })}
        </Box>
    );
};

export default OrderList;
