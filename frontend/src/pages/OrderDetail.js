import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Typography, Box, Grid, Paper, Divider, Chip, CircularProgress, Alert, Button } from '@mui/material';
import { useAuth } from '../components/AuthContext';
import axios from 'axios';

const OrderDetail = () => {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { currentUser } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!currentUser) {
            navigate('/login');
            return;
        }

        const fetchOrderDetail = async () => {
            try {
                setLoading(true);
                const response = await axios.get(`/api/orders/${orderId}`);
                
                // Verificar que el pedido pertenece al usuario actual
                if (response.data.email !== currentUser.email) {
                    setError('No tienes permiso para ver este pedido');
                    setLoading(false);
                    return;
                }
                
                setOrder(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error al obtener el detalle del pedido:', error);
                setError('No se pudo cargar el detalle del pedido. Por favor, inténtelo de nuevo más tarde.');
                setLoading(false);
            }
        };

        fetchOrderDetail();
    }, [orderId, currentUser, navigate]);

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

    const handleBackToOrders = () => {
        navigate('/pedidos');
    };

    if (!currentUser) {
        return null;
    }

    if (loading) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Box display="flex" justifyContent="center" my={4}>
                    <CircularProgress />
                </Box>
            </Container>
        );
    }

    if (error) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>
                <Box mt={2}>
                    <Button variant="outlined" onClick={handleBackToOrders}>Volver a Mis Pedidos</Button>
                </Box>
            </Container>
        );
    }

    if (!order) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Alert severity="warning" sx={{ mt: 2 }}>Pedido no encontrado</Alert>
                <Box mt={2}>
                    <Button variant="outlined" onClick={handleBackToOrders}>Volver a Mis Pedidos</Button>
                </Box>
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4, mb: 6 }}>
            <Paper elevation={3} sx={{ p: { xs: 2, md: 4 } }}>
                <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                    <Button variant="outlined" onClick={handleBackToOrders}>
                        Volver a Mis Pedidos
                    </Button>
                    <Chip 
                        label="Entregado"
                        color="success"
                        variant="filled"
                        sx={{ fontWeight: 'bold', fontSize: '1rem', px: 2 }}
                    />
                </Box>
                <Typography variant="h4" component="h1" fontWeight="bold" gutterBottom>
                    Detalle del Pedido
                </Typography>
                <Typography variant="subtitle1" color="text.secondary" gutterBottom data-testid="order-detail-id">
                    Nº de Pedido: {order.orderId}
                </Typography>
                <Divider sx={{ my: 2 }} />
                <Grid container spacing={3}>
                    {/* Columna Izquierda */}
                    <Grid item xs={12} md={4}>
                        <Grid container direction="column" spacing={3}>
                            <Grid item>
                                {/* Información general del pedido */}
                                <Paper elevation={1} sx={{ p: 2 }}>
                                    <Typography variant="subtitle2" color="text.secondary">Fecha de compra</Typography>
                                    <Typography variant="body1" fontWeight="bold" data-testid="order-detail-date">{formatDate(order.submitDate)}</Typography>
                                    <Divider sx={{ my: 1 }} />
                                    <Typography variant="subtitle2" color="text.secondary">Total</Typography>
                                    <Typography variant="body1" fontWeight="bold">
                                        {order.priceInfo?.total ? `${order.priceInfo.total.toFixed(2)} €` : '-'}
                                    </Typography>
                                    <Divider sx={{ my: 1 }} />
                                    <Typography variant="subtitle2" color="text.secondary">Canal</Typography>
                                    <Typography variant="body1">Online</Typography>
                                </Paper>
                            </Grid>
                            <Grid item>
                                {/* Datos de envío */}
                                <Paper elevation={1} sx={{ p: 2 }}>
                                    <Typography variant="h6" fontWeight="bold" gutterBottom>Datos de envío</Typography>
                                    <Box mt={2}>
                                        {order.shippingAddress && (
                                            <>
                                                <Typography variant="body1" fontWeight="bold">
                                                    {order.shippingAddress.firstName} {order.shippingAddress.lastName}
                                                </Typography>
                                                <Typography variant="body1">{order.shippingAddress.addressLine1}</Typography>
                                                {order.shippingAddress.addressLine2 && (
                                                    <Typography variant="body1">{order.shippingAddress.addressLine2}</Typography>
                                                )}
                                                <Typography variant="body1">
                                                    {order.shippingAddress.postalCode} {order.shippingAddress.city}
                                                </Typography>
                                                <Typography variant="body1">{order.shippingAddress.state}</Typography>
                                                <Typography variant="body1">{order.shippingAddress.country}</Typography>
                                                {order.shippingAddress.phoneNumber && (
                                                    <Typography variant="body1" mt={1}>
                                                        Tel: {order.shippingAddress.phoneNumber}
                                                    </Typography>
                                                )}
                                            </>
                                        )}
                                    </Box>
                                </Paper>
                            </Grid>
                        </Grid>
                    </Grid>

                    {/* Columna Derecha */}
                    <Grid item xs={12} md={8}>
                        <Grid container direction="column" spacing={3}>
                            <Grid item>
                                {/* Productos */}
                                <Paper elevation={1} sx={{
                                    p: 2,
                                    background: 'linear-gradient(135deg, #e3f2fd 0%, #ffffff 100%)',
                                    boxShadow: '0 2px 10px 0 rgba(33,150,243,0.05)'
                                }}>
                                    <Typography variant="h6" fontWeight="bold" gutterBottom>Productos</Typography>
                                    <Box>
                                        {order.shippingGroups && order.shippingGroups.map((group) => (
                                            <React.Fragment key={group.id}>
                                                {group.lineItems && group.lineItems.map((item, index) => (
                                                    <Box key={item.id}>
                                                        <Grid container spacing={2} alignItems="center">
                                                            <Grid item xs={3} md={2}>
                                                                <Box
                                                                    sx={{
                                                                        height: 70,
                                                                        display: 'flex',
                                                                        justifyContent: 'center',
                                                                        alignItems: 'center',
                                                                        border: '1px solid #eee',
                                                                        bgcolor: 'grey.50',
                                                                        borderRadius: 1
                                                                    }}
                                                                >
                                                                    {item.productImageUrl ? (
                                                                        <img
                                                                            src={`/${item.productImageUrl}`}
                                                                            alt={item.productName}
                                                                            style={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' }}
                                                                        />
                                                                    ) : (
                                                                        <Box sx={{ bgcolor: 'grey.200', width: '100%', height: '100%' }} />
                                                                    )}
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={9} md={7}>
                                                                <Typography variant="subtitle1" fontWeight="bold">
                                                                    {item.productName}
                                                                </Typography>
                                                                {item.attributes && Object.entries(item.attributes).map(([key, value]) => (
                                                                    <Typography key={key} variant="body2" color="text.secondary">
                                                                        {key}: {value}
                                                                    </Typography>
                                                                ))}
                                                            </Grid>
                                                            <Grid item xs={12} md={3} sx={{ textAlign: { md: 'right' } }}>
                                                                <Typography variant="subtitle1" fontWeight="bold">
                                                                    {item.totalPrice ? `${item.totalPrice.toFixed(2)} €` : '-'}
                                                                </Typography>
                                                                <Typography variant="body2">
                                                                    {item.quantity} {item.quantity === 1 ? 'unidad' : 'unidades'}
                                                                </Typography>
                                                                {item.unitPrice && (
                                                                    <Typography variant="body2" color="text.secondary">
                                                                        {item.unitPrice.toFixed(2)} € / unidad
                                                                    </Typography>
                                                                )}
                                                            </Grid>
                                                        </Grid>
                                                        {index < group.lineItems.length - 1 && <Divider sx={{ my: 2 }} />}
                                                    </Box>
                                                ))}
                                            </React.Fragment>
                                        ))}
                                    </Box>
                                </Paper>
                            </Grid>
                            <Grid item>
                                {/* Resumen de costes */}
                                <Paper elevation={1} sx={{
                                    p: 2,
                                    background: 'linear-gradient(135deg, #fffde7 0%, #ffffff 100%)',
                                    boxShadow: '0 2px 10px 0 rgba(255,235,59,0.06)'
                                }}>
                                    <Typography variant="h6" fontWeight="bold" gutterBottom>Resumen de costes</Typography>
                                    <Box mt={2} display="flex" flexDirection="column" gap={1.5}>
                                        <Box display="flex" justifyContent="space-between">
                                            <Typography variant="body1">Subtotal</Typography>
                                            <Typography variant="body1">
                                                {order.priceInfo?.subtotal ? `${order.priceInfo.subtotal.toFixed(2)} €` : '-'}
                                            </Typography>
                                        </Box>
                                        <Box display="flex" justifyContent="space-between">
                                            <Typography variant="body1">Impuestos (IVA)</Typography>
                                            <Typography variant="body1">
                                                {order.priceInfo?.tax ? `${order.priceInfo.tax.toFixed(2)} €` : '0.00 €'}
                                            </Typography>
                                        </Box>
                                        {order.shippingGroups && order.shippingGroups[0]?.shippingCost > 0 && (
                                            <Box display="flex" justifyContent="space-between">
                                                <Typography variant="body1">Gastos de envío</Typography>
                                                <Typography variant="body1">
                                                    {`${order.shippingGroups[0].shippingCost.toFixed(2)} €`}
                                                </Typography>
                                            </Box>
                                        )}
                                        {order.priceInfo?.discount && order.priceInfo.discount > 0 && (
                                            <Box display="flex" justifyContent="space-between">
                                                <Typography variant="body1">Descuento</Typography>
                                                <Typography variant="body1" color="error">
                                                    -{order.priceInfo.discount.toFixed(2)} €
                                                </Typography>
                                            </Box>
                                        )}
                                        <Divider sx={{ my: 1 }} />
                                        <Box display="flex" justifyContent="space-between" sx={{ fontWeight: 'bold' }}>
                                            <Typography variant="h6">Total</Typography>
                                            <Typography variant="h6" fontWeight="bold">
                                                {order.priceInfo?.total ? `${order.priceInfo.total.toFixed(2)} €` : '-'}
                                            </Typography>
                                        </Box>
                                    </Box>
                                </Paper>
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </Paper>
        </Container>
    );
};

export default OrderDetail;
