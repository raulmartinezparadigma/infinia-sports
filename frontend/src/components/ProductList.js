import React, { useState, useEffect } from "react";

// Lista de productos con filtros

import Box from "@mui/material/Box";
import ProductCard from "./ProductCard";
import Typography from "@mui/material/Typography";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";
import Pagination from "@mui/material/Pagination";

// Lista de productos con filtros
function ProductList({ searchTerm = "" }) {
  // Estado para productos, loading y error
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Cargar productos del API al montar el componente
  useEffect(() => {
    setLoading(true);
    setError(null);
    fetch("/productos") // Endpoint correcto según backend
      .then((res) => {
        if (!res.ok) throw new Error("Error al cargar productos");
        return res.json();
      })
      .then((data) => {
        setProducts(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  // Filtro por término de búsqueda
  const filtered = products.filter(
    (p) =>
      (p.name && p.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (p.description && p.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  // Estado de paginación
  const [page, setPage] = useState(1);
  const itemsPerPage = 9;
  const totalPages = Math.ceil(filtered.length / itemsPerPage);

  // Productos a mostrar en la página actual
  const paginated = filtered.slice((page - 1) * itemsPerPage, page * itemsPerPage);

  // Resetear a la primera página si cambia el filtro
  useEffect(() => {
    setPage(1);
  }, [searchTerm, products]);

  return (
    <Box sx={{ mt: 2 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>Catálogo de productos</Typography>
      {loading && <CircularProgress />}
      {error && <Alert severity="error">{error}</Alert>}
      {!loading && !error && (
        <>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', width: '100%' }}>
            {[...paginated, ...Array(9 - paginated.length).fill(null)].map((product, idx) => (
              <Box key={product ? product.id : `empty-${idx}`}
                sx={{ flex: '0 0 33.33%', maxWidth: '33.33%', p: 2, boxSizing: 'border-box', display: 'flex', justifyContent: 'center' }}>
                {product ? (
                  <ProductCard product={product} sx={{ width: 320, minHeight: 380, maxWidth: 340 }} />
                ) : null}
              </Box>
            ))}
          </Box>
          {filtered.length === 0 && (
            <Typography variant="body1" sx={{ mt: 4 }}>No se encontraron productos.</Typography>
          )}
          {/* Controles de paginación con Material-UI */}
          {filtered.length > itemsPerPage && (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 4 }}>
              {/* Componente Pagination de Material-UI */}
              <Pagination
                count={totalPages}
                page={page}
                onChange={(e, value) => setPage(value)}
                color="primary"
                size="large"
                showFirstButton
                showLastButton
                sx={{
                  background: '#f5f7fa',
                  borderRadius: 2,
                  boxShadow: '0 2px 8px 0 rgba(50, 50, 80, 0.10)',
                  p: 1,
                  '& .Mui-selected': {
                    backgroundColor: '#1976d2 !important',
                    color: '#fff',
                  }
                }}
              />
            </Box>
          )}
        </>
      )}
    </Box>
  );
}



export default ProductList;
