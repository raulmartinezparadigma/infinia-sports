import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PersonIcon from '@mui/icons-material/Person';
import LoginIcon from '@mui/icons-material/Login';
import TextField from '@mui/material/TextField';

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
  
  const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);
  
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
    <nav style={{ padding: '42px 32px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 1px 0 #e0e0e0' }}>
      {/* Logo */}
      <Link to="/" style={{ display: 'flex', alignItems: 'center', textDecoration: 'none' }}>
        <img src={process.env.PUBLIC_URL + '/infinia_sports.jpg'} alt="Infinia Sports" style={{ height: 180, objectFit: 'contain' }} />
      </Link>

      {/* Buscador centrado */}
      <div style={{ flex: 1, display: 'flex', justifyContent: 'center', padding: '0 24px' }}>
        <TextField
          placeholder="Buscar productos"
          variant="outlined"
          size="small"
          value={search}
          onChange={e => {
            const val = e.target.value;
            setSearch(val);
            const path = location.pathname === '/' ? '/catalog' : location.pathname;
            navigate(`${path}?query=${encodeURIComponent(val)}`, { replace: true });
          }}
          sx={{ width: '100%', maxWidth: 600, background: '#fff' }}
        />
      </div>

      {/* Menú usuario / carrito */}
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {currentUser ? (
          <>
            <Button 
              color="inherit" 
              startIcon={<PersonIcon />}
              onClick={handleMenuOpen}
              sx={{ color: '#1a237e', mr: 2 }}
            >
              {currentUser.username}
            </Button>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
            >
              <MenuItem onClick={() => { handleMenuClose(); navigate('/profile'); }}>
                Mi Perfil
              </MenuItem>
              <MenuItem onClick={() => { handleMenuClose(); navigate('/orders'); }}>
                Mis Pedidos
              </MenuItem>
              <MenuItem onClick={handleLogout}>Cerrar Sesión</MenuItem>
            </Menu>
          </>
        ) : (
          <>
            <Button 
              color="inherit" 
              startIcon={<LoginIcon />}
              onClick={handleLogin}
              sx={{ color: '#1a237e', mr: 1 }}
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
        
        <Link to="/cart">
          <IconButton sx={{ color: '#1a237e' }}>
            <Badge badgeContent={totalCount} color="error">
              <ShoppingCartIcon />
            </Badge>
          </IconButton>
        </Link>
      </div>
    </nav>
  );
}

export default Navbar;
