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
      background: '#e0e7ff', // Fondo general gris claro
      padding: '40px 0',
      position: 'relative',
    }}>
      {/* Banner tipo hero */}
      <div style={{ maxWidth: 1100, margin: '0 auto', marginBottom: 36 }}>
        <div
          style={{
            minHeight: 260,
            background: '#fff', // Banner hero blanco
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '32px 48px 24px 48px',
            position: 'relative',
            boxSizing: 'border-box',
            borderRadius: 16,
            boxShadow: '0 2px 16px #b3c6ff33'
          }}
        >
          {/* Logo grande a la izquierda */}
          <div style={{ minWidth: 220, display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
            <img
              src={process.env.PUBLIC_URL + '/infinia_sports.jpg'}
              alt="Infinia Sports logo"
              style={{ maxHeight: 120, maxWidth: 220, objectFit: 'contain', marginBottom: 12 }}
            />
            <span style={{ fontWeight: 700, color: '#1a237e', fontSize: 18 }}>¡Equípate como un pro!</span>
          </div>
          {/* Título y buscador centrados */}
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <h1 style={{
              fontSize: 46,
              fontWeight: 900,
              color: '#1a237e',
              margin: 0,
              letterSpacing: 2,
              textShadow: '0 2px 8px #b3c6ff',
              fontFamily: `'Bangers', 'Luckiest Guy', 'Permanent Marker', 'Comic Sans MS', 'cursive', 'sans-serif'`
            }}>
              Nuestros productos
            </h1>
            <div style={{ marginTop: 18, width: 340 }}>
              <SearchBar value={searchTerm} onChange={handleSearch} />
            </div>
            <div style={{ marginTop: 12, color: '#374151', fontWeight: 500 }}>
              ¡Zapatillas, ropa y suplementos premium en un solo lugar!
            </div>
          </div>
          {/* Producto destacado a la derecha */}
          <div style={{
            minWidth: 220,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-end',
            justifyContent: 'flex-end'
          }}>
            <img
              src={process.env.PUBLIC_URL + '/infinia_sports.jpg'}
              alt="Producto destacado"
              style={{ maxHeight: 140, maxWidth: 180, objectFit: 'contain', display: 'block' }}
            />
          </div>
        </div>
      </div>

      {/* Lista de productos debajo del banner */}
      <div style={{ maxWidth: 1100, margin: '0 auto' }}>
        <ProductList searchTerm={searchTerm} />
      </div>

      {/* Franja de ventajas */}
      <div style={{
        maxWidth: 1100,
        margin: '32px auto 0 auto',
        background: '#f3f4f6',
        borderRadius: 14,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '24px 32px',
        gap: 12,
        boxShadow: '0 1px 8px #b3c6ff22',
        flexWrap: 'wrap'
      }}>
        <div style={{ flex: 1, minWidth: 180, textAlign: 'center' }}>
          <div style={{ fontSize: 30 }}>⏰</div>
          <div style={{ fontWeight: 700, color: '#1a237e', marginBottom: 4 }}>ENTREGAS 24H</div>
          <div style={{ fontSize: 14, color: '#374151' }}>Recibe tu pedido en 24h o 48h si es festivo.</div>
        </div>
        <div style={{ flex: 1, minWidth: 180, textAlign: 'center' }}>
          <div style={{ fontSize: 30 }}>🚚</div>
          <div style={{ fontWeight: 700, color: '#1a237e', marginBottom: 4 }}>ENVÍOS GRATIS</div>
          <div style={{ fontSize: 14, color: '#374151' }}>En pedidos superiores a 25&nbsp;€ envío gratis.</div>
        </div>
        <div style={{ flex: 1, minWidth: 180, textAlign: 'center' }}>
          <div style={{ fontSize: 30 }}>🔒</div>
          <div style={{ fontWeight: 700, color: '#1a237e', marginBottom: 4 }}>PAGO SEGURO</div>
          <div style={{ fontSize: 14, color: '#374151' }}>Certificado SSL y métodos de pago seguros.</div>
        </div>
        <div style={{ flex: 1, minWidth: 180, textAlign: 'center' }}>
          <div style={{ fontSize: 30 }}>💬</div>
          <div style={{ fontWeight: 700, color: '#1a237e', marginBottom: 4 }}>ATENCIÓN RÁPIDA</div>
          <div style={{ fontSize: 14, color: '#374151' }}>Clientes satisfechos y soporte ágil.</div>
        </div>
      </div>

    </div>
  );
}

export default Catalog;
