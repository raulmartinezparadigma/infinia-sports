import React, { useState } from 'react';
import './App.css';
import OrderSearchForm from './OrderSearchForm';
import OrderDetailsDisplay from './OrderDetailsDisplay';
import { fetchOrderById } from './api';

// Componente principal del panel de administración
function App() {
  // Estado para el pedido consultado y control de "no encontrado"
  const [order, setOrder] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [loading, setLoading] = useState(false);

  // Maneja la búsqueda por orderId
  const handleSearch = async (orderId) => {
    setLoading(true);
    setNotFound(false);
    setOrder(null);
    try {
      const result = await fetchOrderById(orderId);
      if (result) {
        setOrder(result);
      } else {
        setNotFound(true);
      }
    } catch (e) {
      setNotFound(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Panel de Administración de Pedidos</h1>
      </header>
      {/* Formulario de búsqueda por Order ID */}
      <OrderSearchForm onSearch={handleSearch} />
      {loading && <p>Cargando...</p>}
      {/* Muestra los detalles del pedido o mensaje de no encontrado */}
      <OrderDetailsDisplay order={order} notFound={notFound} />
    </div>
  );
}

export default App;
