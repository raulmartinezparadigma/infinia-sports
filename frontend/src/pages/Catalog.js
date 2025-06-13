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
      background: '#fff', // Fondo completamente blanco
      padding: '40px 0',
      position: 'relative',
    }}>
      <div style={{
        maxWidth: 1100,
        margin: '0 auto',
        background: '#fff',
        borderRadius: 0,
        boxShadow: 'none',
        border: 'none',
        overflow: 'hidden',
        padding: '32px 24px 24px 24px',
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'flex-start', // Alinea arriba
      }}>
        {/* Logo grande alineado arriba a la izquierda, dentro del contenedor blanco */}
        <div style={{ minWidth: 220, width: 300, height: 180, display: 'flex', alignItems: 'flex-start', justifyContent: 'flex-start', marginRight: 36, marginTop: 0 }}>
          <img src={process.env.PUBLIC_URL + '/infinia_sports.jpg'} alt="Infinia Sports logo" style={{ maxHeight: 180, maxWidth: 300, objectFit: 'contain', display: 'block', marginTop: 0 }} />
        </div>
        {/* Contenido principal del catálogo a la derecha del logo */}
        <div style={{ flex: 1 }}>
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
    </div>
  );
}

export default Catalog;
