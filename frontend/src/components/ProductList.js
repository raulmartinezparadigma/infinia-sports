import React, { useState, useEffect } from "react";

// Lista de productos con filtros

import Box from "@mui/material/Box";
import ProductCard from "./ProductCard";
import Typography from "@mui/material/Typography";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";
import Pagination from "@mui/material/Pagination";
import Button from '@mui/material/Button';
import Drawer from '@mui/material/Drawer';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Slider from '@mui/material/Slider';
import Divider from '@mui/material/Divider';


// Lista de productos con filtros
function ProductList({ searchTerm = "" }) {
  // Añade fondo blanco solo mientras está montado este componente (catálogo)
  React.useEffect(() => {
    document.body.classList.add('catalog-page');
    return () => document.body.classList.remove('catalog-page');
  }, []);
  // Estado para productos, loading y error
  const [products, setProducts] = useState([]);

  // Estado para Drawer de filtros y orden
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [sortOrder, setSortOrder] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [priceRange, setPriceRange] = useState([0, 200]); // Rango ejemplo

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

  // Estado para aplicar filtros/orden solo al pulsar 'Aplicar'
  const [appliedFilters, setAppliedFilters] = useState({ sortOrder: '', typeFilter: '', priceRange: [0, 200] });

  // Lógica de filtrado y ordenado
  const filtered = products
    .filter((p) => {
      // Filtro por término de búsqueda
      const matchesSearch =
        (p.name && p.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (p.description && p.description.toLowerCase().includes(searchTerm.toLowerCase()));
      // Filtro por tipo
      const matchesType = !appliedFilters.typeFilter || (p.type === appliedFilters.typeFilter);
      // Filtro por rango de precio
      const matchesPrice = typeof p.price === 'number' &&
        p.price >= appliedFilters.priceRange[0] &&
        p.price <= appliedFilters.priceRange[1];
      return matchesSearch && matchesType && matchesPrice;
    })
    .sort((a, b) => {
      switch (appliedFilters.sortOrder) {
        case 'price-asc':
          return (a.price ?? 0) - (b.price ?? 0);
        case 'price-desc':
          return (b.price ?? 0) - (a.price ?? 0);
        case 'new':
          // Novedades: id descendente (simulación, si hay campo date usarlo)
          return (b.id || '').localeCompare(a.id || '');
        case 'bestseller':
          // Más vendidos: usar campo ficticio 'sales' si existe
          return (b.sales ?? 0) - (a.sales ?? 0);
        default:
          return 0;
      }
    });

  // Estado de paginación
  const [page, setPage] = useState(1);
  const itemsPerPage = 9;
  const totalPages = Math.ceil(filtered.length / itemsPerPage);

  // Productos a mostrar en la página actual
  const paginated = filtered.slice((page - 1) * itemsPerPage, page * itemsPerPage);

  // Resetear a la primera página si cambia el filtro
  useEffect(() => {
    setPage(1);
  }, [searchTerm, products, appliedFilters]);

  return (
    <>
    {/* Encabezado eliminado por solicitud del usuario. Solo queda el catálogo limpio */}
    <Box sx={{ mt: 2, background: '#fff', boxShadow: 'none', borderRadius: 0, border: 'none', position: 'relative' }}>
      {/* Botón Filtrar y ordenar */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <Button
          variant="outlined"
          color="primary"
          sx={{ mr: 2 }}
          onClick={() => setDrawerOpen(true)}
        >
          Filtrar y ordenar
        </Button>
      </Box>

      {/* Drawer lateral */}
      <Drawer
        anchor="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
      >
        <Box sx={{ width: 320, p: 3 }} role="presentation">
          <Typography variant="h6" gutterBottom>Filtrar y ordenar</Typography>
          <Divider sx={{ mb: 2 }} />
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel id="sort-label">Ordenar por</InputLabel>
            <Select
              labelId="sort-label"
              value={sortOrder}
              label="Ordenar por"
              onChange={e => setSortOrder(e.target.value)}
            >
              <MenuItem value="">Ninguno</MenuItem>
              <MenuItem value="price-asc">Precio: menor a mayor</MenuItem>
              <MenuItem value="price-desc">Precio: mayor a menor</MenuItem>
              <MenuItem value="new">Novedades</MenuItem>
              <MenuItem value="bestseller">Más vendidos</MenuItem>
            </Select>
          </FormControl>
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel id="type-label">Tipo</InputLabel>
            <Select
              labelId="type-label"
              value={typeFilter}
              label="Tipo"
              onChange={e => setTypeFilter(e.target.value)}
            >
              <MenuItem value="">Todos</MenuItem>
              <MenuItem value="SNEAKERS">Zapatillas</MenuItem>
              <MenuItem value="CLOTHING">Ropa</MenuItem>
              <MenuItem value="SUPPLEMENT">Suplemento</MenuItem>
            </Select>
          </FormControl>
          <Typography gutterBottom sx={{ mt: 2 }}>Precio</Typography>
          <Slider
            value={priceRange}
            onChange={(_, val) => setPriceRange(val)}
            valueLabelDisplay="auto"
            min={0}
            max={200}
            sx={{ mb: 2 }}
          />
          <Button
            variant="contained"
            color="primary"
            fullWidth
            sx={{ mt: 2 }}
            onClick={() => {
              setAppliedFilters({
                sortOrder,
                typeFilter,
                priceRange,
              });
              setDrawerOpen(false);
            }}
          >
            Aplicar
          </Button>
        </Box>
      </Drawer>

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
                  background: '#fff', // Fondo blanco liso
                  borderRadius: 0,
                  boxShadow: 'none',
                  border: 'none',
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
    </>
  );
}



export default ProductList;
