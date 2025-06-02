import React, { useState } from "react";
// Página de catálogo de productos
import SearchBar from "../components/SearchBar";
import ProductList from "../components/ProductList";

function Catalog() {
  // Estado para el término de búsqueda
  const [searchTerm, setSearchTerm] = useState("");

  // Manejar cambios en la barra de búsqueda
  const handleSearch = (e) => setSearchTerm(e.target.value);

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
      padding: '40px 0',
    }}>
      <div style={{
        position: 'relative',
        maxWidth: 950,
        margin: '0 auto',
        borderRadius: 20,
        boxShadow: '0 8px 32px 0 rgba(50, 50, 80, 0.15)',
        padding: '32px 24px 24px 24px',
        background: 'rgba(255,255,255,0.95)',
        border: '3px solid #e0e7ef',
        overflow: 'hidden',
      }}>
        {/* Imagen decorativa de zapatillas (esquina inferior izquierda) */}

        <h2 style={{
          color: '#1a237e',
          fontWeight: 900,
          letterSpacing: 1.5,
          fontSize: 44,
          textAlign: 'center',
          marginBottom: 18,
          textShadow: '0 2px 8px #b3c6ff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 16,
          fontFamily: `'Oswald', 'Montserrat', 'Bebas Neue', Arial, sans-serif'`
        }}>
          <span role="img" aria-label="zapatillas">👟</span>
          Catálogo de Productos
          <span role="img" aria-label="pesas">🏋️‍♂️</span>
        </h2>
        <div style={{ margin: '0 auto 30px auto', maxWidth: 400 }}>
          <SearchBar value={searchTerm} onChange={handleSearch} />
        </div>
        <div style={{marginBottom: 8, textAlign: 'center', color: '#374151', fontWeight: 500}}>
          ¡Equípate para tu mejor entrenamiento con zapatillas, ropa y suplementos premium!
        </div>
        <ProductList searchTerm={searchTerm} />
      </div>
    </div>
  );
}

export default Catalog;
