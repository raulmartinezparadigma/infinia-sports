import React from 'react';
import { Box, Button, Typography, Paper, Divider } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import PersonIcon from '@mui/icons-material/Person';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';

/**
 * Componente que muestra opciones para continuar con el checkout:
 * - Como usuario anónimo
 * - Iniciando sesión
 * - Registrándose
 */
function CheckoutOptions({ onContinueAnonymous }) {
  const navigate = useNavigate();

  return (
    <Paper elevation={3} sx={{ maxWidth: 600, mx: 'auto', mt: 4, p: 4 }}>
      <Typography variant="h5" align="center" gutterBottom>
        Opciones de Checkout
      </Typography>
      
      <Typography variant="body1" align="center" sx={{ mb: 3 }}>
        Elige cómo quieres continuar con tu compra
      </Typography>
      
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, gap: 3, mb: 4 }}>
        <Paper 
          elevation={2} 
          sx={{ 
            flex: 1, 
            p: 3, 
            textAlign: 'center',
            border: '1px solid #e0e0e0',
            '&:hover': { borderColor: '#1976d2' }
          }}
        >
          <VisibilityOffIcon sx={{ fontSize: 40, color: '#757575', mb: 1 }} />
          <Typography variant="h6" gutterBottom>Checkout Anónimo</Typography>
          <Typography variant="body2" sx={{ mb: 2 }}>
            Completa tu compra sin crear una cuenta. Rápido y sencillo.
          </Typography>
          <Button 
            variant="contained" 
            color="primary"
            fullWidth
            onClick={onContinueAnonymous}
          >
            Continuar sin registro
          </Button>
        </Paper>
        
        <Divider orientation="vertical" flexItem sx={{ display: { xs: 'none', sm: 'block' } }} />
        <Divider sx={{ display: { xs: 'block', sm: 'none' } }} />
        
        <Paper 
          elevation={2} 
          sx={{ 
            flex: 1, 
            p: 3, 
            textAlign: 'center',
            border: '1px solid #e0e0e0',
            '&:hover': { borderColor: '#1976d2' }
          }}
        >
          <PersonIcon sx={{ fontSize: 40, color: '#1976d2', mb: 1 }} />
          <Typography variant="h6" gutterBottom>Usuario Registrado</Typography>
          <Typography variant="body2" sx={{ mb: 2 }}>
            Inicia sesión o crea una cuenta para guardar tu historial de pedidos.
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <Button 
              variant="contained" 
              onClick={() => navigate('/login', { state: { returnTo: '/checkout' } })}
            >
              Iniciar sesión
            </Button>
            <Button 
              variant="outlined"
              onClick={() => navigate('/register', { state: { returnTo: '/checkout' } })}
            >
              Crear cuenta
            </Button>
          </Box>
        </Paper>
      </Box>
      
      <Box sx={{ textAlign: 'center', mt: 2 }}>
        <Button 
          variant="text" 
          color="primary"
          onClick={() => navigate('/cart')}
        >
          Volver al carrito
        </Button>
      </Box>
    </Paper>
  );
}

export default CheckoutOptions;
