import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import CheckoutStepperBar from './CheckoutStepperBar';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PersonIcon from '@mui/icons-material/Person';
import LoginIcon from '@mui/icons-material/Login';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import SearchIcon from '@mui/icons-material/Search';

import Badge from '@mui/material/Badge';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useCart } from "./CartContext";
import { useAuth } from "./AuthContext";

// Barra de navegación principal
function Navbar() {
  const { cart } = useCart();
  const [search, setSearch] = React.useState('');
  const location = useLocation();
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = React.useState(null);
  
  const totalCount = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;
  
  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };
  
  const handleMenuClose = () => {
    setAnchorEl(null);
  };
  
  const handleLogout = () => {
    logout();
    handleMenuClose();
    navigate('/');
  };
  
  const handleLogin = () => {
    navigate('/login');
  };
  
  const handleRegister = () => {
    navigate('/register');
  };

  return (
    <nav style={{ padding: '42px 32px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 1px 0 #e0e0e0', height: 135 }}>
      {/* Logo */}
      <Link to="/" style={{ display: 'flex', alignItems: 'center', textDecoration: 'none' }}>
        <img src={process.env.PUBLIC_URL + '/infinia_sports.jpg'} alt="Infinia Sports" style={{ height: 216, objectFit: 'contain' }} />
      </Link>

      {/* Buscador centrado o barra de progreso según ruta */}
      {(() => {
        if (["/cart", "/checkout", "/confirmation"].includes(location.pathname)) {
          let step = 0;
          if (location.pathname === "/cart") step = 0;
          else if (location.pathname === "/checkout") {
            const params = new URLSearchParams(location.search);
            step = parseInt(params.get('step'), 10);
            if (isNaN(step)) step = 1;
          } else if (location.pathname === "/confirmation") step = 4;
          return (
            <div style={{ flex: 1, display: 'flex', justifyContent: 'center', padding: '0 24px' }}>
              <CheckoutStepperBar step={step} />
            </div>
          );
        }
        // Si no, muestra el buscador
        return (
          <div style={{ flex: 1, display: 'flex', justifyContent: 'center', padding: '0 24px' }}>
            <TextField
              placeholder="Buscar productos"
              variant="outlined"
              value={search}
              onChange={e => {
                const val = e.target.value;
                setSearch(val);
                const path = location.pathname === '/' ? '/catalog' : location.pathname;
                navigate(`${path}?query=${encodeURIComponent(val)}`, { replace: true });
              }}
              sx={{
                width: '100%', 
                maxWidth: 600, 
                background: '#fff',
                '& .MuiOutlinedInput-root': {
                  borderRadius: '24px', // Bordes redondeados
                },
              }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />
          </div>
        );
      })()}


      {/* Menú usuario / carrito */}
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {currentUser ? (
          <>
            <Button 
              color="inherit" 
              startIcon={<PersonIcon />}
              onClick={handleMenuOpen}
              sx={{ color: '#1a237e', mr: 2 }}
              data-testid="user-menu-button"
            >
              {currentUser.username}
            </Button>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
            >
              <MenuItem onClick={() => { handleMenuClose(); navigate('/profile'); }}>
                <Link to="/profile">Mi Perfil</Link>
              </MenuItem>
              <MenuItem data-testid="my-orders-link" onClick={() => { handleMenuClose(); navigate('/pedidos'); }}>
                Mis Pedidos
              </MenuItem>
              <MenuItem data-testid="logout-button" onClick={handleLogout}>Cerrar Sesión</MenuItem>
            </Menu>
          </>
        ) : (
          <>
            <Button 
              color="inherit" 
              startIcon={<LoginIcon />}
              onClick={handleLogin}
              sx={{ color: '#1a237e', mr: 1 }}
              data-testid="login-button"
            >
              Iniciar Sesión
            </Button>
            <Button 
              color="inherit" 
              startIcon={<PersonIcon />}
              onClick={handleRegister}
              sx={{ color: '#1a237e', mr: 2 }}
            >
              Registrarse
            </Button>
          </>
        )}
        
        <IconButton component={Link} to="/cart" aria-label="cart" sx={{ color: '#1a237e' }}>
          <Badge badgeContent={totalCount} color="error" data-testid="cart-badge">
            <ShoppingCartIcon />
          </Badge>
        </IconButton>
      </div>
    </nav>
  );
}

export default Navbar;
