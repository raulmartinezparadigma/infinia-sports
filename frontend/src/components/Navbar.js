import React from "react";
import { Link, useNavigate } from "react-router-dom";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PersonIcon from '@mui/icons-material/Person';
import LoginIcon from '@mui/icons-material/Login';
import LogoutIcon from '@mui/icons-material/Logout';
import Badge from '@mui/material/Badge';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Typography from '@mui/material/Typography';
import { useCart } from "./CartContext";
import { useAuth } from "./AuthContext";

// Barra de navegación principal
function Navbar() {
  const { cart } = useCart();
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
    <nav style={{ padding: "1rem", background: "#1976d2", display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
        <Typography variant="h6" component="div">
          Infinia Sports
        </Typography>
      </Link>
      
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {currentUser ? (
          <>
            <Button 
              color="inherit" 
              startIcon={<PersonIcon />}
              onClick={handleMenuOpen}
              sx={{ color: 'white', mr: 2 }}
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
              sx={{ color: 'white', mr: 1 }}
            >
              Iniciar Sesión
            </Button>
            <Button 
              color="inherit" 
              startIcon={<PersonIcon />}
              onClick={handleRegister}
              sx={{ color: 'white', mr: 2 }}
            >
              Registrarse
            </Button>
          </>
        )}
        
        <Link to="/cart">
          <IconButton sx={{ color: '#fff' }}>
            <Badge badgeContent={totalCount} color="error">
              <ShoppingCartIcon />
            </Badge>
          </IconButton>
        </Link>
      </div>
    <nav style={{ padding: "1rem", background: "#1976d2", display: 'flex', alignItems: 'center' }}>
      {/* Enlace al panel de administración */}
      <a
        href="http://localhost:3001"
        target="_blank"
        rel="noopener noreferrer"
        style={{ color: '#fff', fontWeight: 'bold', marginRight: 24, textDecoration: 'none', background: '#1565c0', padding: '8px 16px', borderRadius: 4 }}
      >
        Panel de Administración
      </a>

      <div style={{ flex: 1 }} />

      <Link to="/cart">
        <IconButton sx={{ color: '#fff' }}>
          <Badge badgeContent={totalCount} color="error">
            <ShoppingCartIcon />
          </Badge>
        </IconButton>
      </Link>
    </nav>
  );
}

export default Navbar;
