import React from "react";

// Barra de búsqueda de productos
import TextField from "@mui/material/TextField";
import Box from "@mui/material/Box";

// Barra de búsqueda de productos
function SearchBar({ value, onChange }) {
  // value: string, onChange: function
  return (
    <Box sx={{ mb: 2 }}>
      <TextField
        label="Buscar productos"
        variant="outlined"
        value={value}
        onChange={onChange}
        fullWidth
        size="small"
      />
    </Box>
  );
}


export default SearchBar;
