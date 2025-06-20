import React, { useState } from 'react';
import './App.css';
import OrderSearchForm from './OrderSearchForm';
import OrderDetailsDisplay from './OrderDetailsDisplay';
import { fetchOrderById } from './api';
import AdminKafkaPanel from './AdminKafkaPanel';
import AdminLogin from './AdminLogin';

// Componente principal del panel de administración
function App() {
  // Estado de autenticación admin
  const [token, setToken] = useState(localStorage.getItem('admin_jwt'));

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

  const [tab, setTab] = useState('orders');

  // Logout
  const handleLogout = () => {
    localStorage.removeItem('admin_jwt');
    setToken(null);
  };

  // Si no está autenticado, mostrar login
  if (!token) {
    return <AdminLogin onLogin={setToken} />;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Panel de Administración</h1>
        <button onClick={handleLogout} style={{ float: 'right', marginTop: 8 }}>Cerrar sesión</button>
        <div style={{marginTop: 16, marginBottom: 24}}>
          <button onClick={() => setTab('orders')} style={{marginRight: 12, fontWeight: tab==='orders'?'bold':'normal'}}>Pedidos</button>
          <button onClick={() => setTab('kafka')} style={{fontWeight: tab==='kafka'?'bold':'normal'}}>Alta productos</button>
        </div>
      </header>
      {tab === 'orders' && (
        <>
          <OrderSearchForm onSearch={handleSearch} />
          {loading && <p>Cargando...</p>}
          <OrderDetailsDisplay order={order} notFound={notFound} />
        </>
      )}
      {tab === 'kafka' && (
        <AdminKafkaPanel />
      )}
    </div>
  );
}

export default App;
