import React from "react";
import { useLocation } from "react-router-dom";
// Página de catálogo de productos
import ProductList from "../components/ProductList";

function Catalog() {
    // Leer término de búsqueda de la URL (?query=)
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const searchTerm = params.get('query') || '';

  return (
    <div style={{
      minHeight: '100vh',
      padding: '40px 0',
      position: 'relative',
    }}>
      

      {/* Lista de productos debajo del banner */}
      <div style={{ width: '100%', margin: '0 auto', padding: 0 }}>
        <ProductList searchTerm={searchTerm} />
      </div>

      {/* Franja de ventajas */}
      <div style={{
        width: '100%',
        margin: '32px 0 0 0',
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
