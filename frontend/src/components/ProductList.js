import React, { useState, useEffect } from "react";

// Lista de productos con filtros

import Box from "@mui/material/Box";
import ProductCard from "./ProductCard";
import CategoryBar from "./CategoryBar";
import Typography from "@mui/material/Typography";
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
  // Estado para productos
  const [products, setProducts] = useState([]);

  // Estado para Drawer de filtros y orden
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [sortOrder, setSortOrder] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  // Estado para barra de categorías
  const [category, setCategory] = useState('');
  const [priceRange, setPriceRange] = useState([0, 200]); // Rango ejemplo

  // Estado para aplicar filtros/orden solo al pulsar 'Aplicar'
  const [appliedFilters, setAppliedFilters] = useState({ sortOrder: '', typeFilter: '', priceRange: [0, 200] });

  // Cargar productos del API al montar el componente
  useEffect(() => {
    fetch("/api/products") // Endpoint correcto según backend
      .then((response) => {
        if (!response.ok) throw new Error("Error al cargar productos");
        return response.json();
      })
      .then((data) => {
        setProducts(data);
      })
      .catch((err) => {
        console.error("Error al cargar productos:", err);
      });
  }, []);

  // Lógica de filtrado y ordenado
  const filtered = products
    .filter((p) => {
      // Filtro por término de búsqueda
      const matchesSearch =
        (p.name && p.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (p.description && p.description.toLowerCase().includes(searchTerm.toLowerCase()));
      // Filtro por tipo: si hay categoría visual, priorizarla; si no, usar la del Drawer
      let matchesType = true;
      if (category && ["SNEAKERS", "CLOTHING", "SUPPLEMENT"].includes(category)) {
        matchesType = p.type === category;
      } else if (appliedFilters.typeFilter && ["SNEAKERS", "CLOTHING", "SUPPLEMENT"].includes(appliedFilters.typeFilter)) {
        matchesType = p.type === appliedFilters.typeFilter;
      }
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
  const itemsPerPage = 12;
  const totalPages = Math.ceil(filtered.length / itemsPerPage);

  // Productos a mostrar en la página actual
  const paginated = filtered.slice((page - 1) * itemsPerPage, page * itemsPerPage);

  // Resetear a la primera página si cambia el filtro
  useEffect(() => {
    setPage(1);
  }, [searchTerm, products, appliedFilters]);

  return (
    <>
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

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2, mb: 2, background: '#fff', borderRadius: 2, boxShadow: '0 2px 8px #b3c6ff22', px: 2, py: 1 }}>
        <CategoryBar selected={category} onSelect={setCategory} />
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Button
            variant="outlined"
            color="primary"
            sx={{ mr: 2, minWidth: 140, height: 40 }}
            onClick={() => setDrawerOpen(true)}
          >
            Filtrar y ordenar
          </Button>
          {/* Contador de artículos */}
          <span style={{ fontWeight: 500, color: '#555', marginLeft: 8, fontSize: 16 }}>
            {filtered.length} artículos
          </span>
        </Box>
      </Box>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: {
            xs: '1fr',
            sm: '1fr 1fr',
            md: '1fr 1fr 1fr 1fr'
          },
          gap: 1.5,
          width: '100%',
          px: { xs: 0.5, sm: 1, md: 2 },
          py: 2,
          boxSizing: 'border-box',
          alignItems: 'stretch',
          justifyItems: 'center',
          margin: 0
        }}
      >
        {paginated.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </Box>

      {filtered.length === 0 && (
        <Typography variant="body1" sx={{ mt: 4, ml: 4 }}>No se encontraron productos.</Typography>
      )}

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
  );
}

export default ProductList;
