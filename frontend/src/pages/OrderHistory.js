import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, CircularProgress, Alert, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../components/AuthContext';
import OrderList from '../components/OrderList';
import axios from 'axios';

const OrderHistory = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { currentUser } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!currentUser) {
            navigate('/login');
            return;
        }

        const fetchOrders = async () => {
            if (!currentUser || !currentUser.email) {
                setLoading(false);
                setError('No hay un usuario autenticado o el usuario no tiene email');
                return;
            }

            try {
                setLoading(true);
                // Usar URL relativa para aprovechar la configuración de proxy
                const response = await axios.get(`/api/orders?email=${encodeURIComponent(currentUser.email)}`);
                console.log('Respuesta de órdenes:', response.data);
                setOrders(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error al obtener los pedidos:', error);
                setError(`Error: ${error.message}. Código: ${error.response?.status || 'desconocido'}. Detalles: ${error.response?.data?.message || 'No disponible'}`);
                setLoading(false);
            }
        };

        fetchOrders();
    }, [currentUser, navigate]);

    if (!currentUser) {
        return null; 
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Mis Pedidos
            </Typography>
            
            {loading ? (
                <Box display="flex" justifyContent="center" my={4}>
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>
            ) : orders.length === 0 ? (
                <Alert severity="info" sx={{ mt: 2 }}>
                    No tienes pedidos realizados todavía.
                </Alert>
            ) : (
                <OrderList orders={orders} />
            )}
        </Container>
    );
};

export default OrderHistory;
