// OrderSearchForm.js
// Formulario para buscar pedido por orderId
import React, { useState } from 'react';

function OrderSearchForm({ onSearch }) {
  const [orderId, setOrderId] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (orderId.trim()) {
      onSearch(orderId.trim());
    }
  };

  return (
    <form className="order-search-form" onSubmit={handleSubmit}>
      <label htmlFor="orderId">Id del pedido: </label>
      <input
        type="text"
        id="orderId"
        value={orderId}
        onChange={e => setOrderId(e.target.value)}
        placeholder="Introduce el Id del pedido"
      />
      <button type="submit">Buscar</button>
    </form>
  );
}

export default OrderSearchForm;
