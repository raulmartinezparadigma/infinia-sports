import React from "react";
import { Link } from "react-router-dom";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import Badge from '@mui/material/Badge';
import IconButton from '@mui/material/IconButton';
import { useCart } from "./CartContext";

// Barra de navegación principal
function Navbar() {
  const { cart } = useCart();
  const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <nav style={{ padding: "1rem", background: "#1976d2", display: 'flex', alignItems: 'center' }}>

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
