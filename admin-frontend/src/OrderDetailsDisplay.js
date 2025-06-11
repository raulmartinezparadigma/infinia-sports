// OrderDetailsDisplay.js
// Muestra los detalles relevantes de un pedido
import React from 'react';

function OrderDetailsDisplay({ order, notFound }) {
  if (notFound) {
    return (
      <div className="order-details-display">
        <h2>Pedido no encontrado</h2>
        <p>Verifica el Order ID e inténtalo de nuevo.</p>
      </div>
    );
  }
  if (!order) return null;

  // Desestructuramos los campos más relevantes
  const {
    id,
    status,
    totalAmount,
    createdAt,
    email,
    shippingAddress,
    billingAddress,
    paymentMethod,
    paymentStatus,
    items = []
  } = order;

  return (
    <div className="order-details-display">
      <h2>Detalles del Pedido</h2>
      <p><strong>ID:</strong> {id}</p>
      <p><strong>Estado:</strong> {status}</p>
      <p><strong>Monto total:</strong> {totalAmount} €</p>
      <p><strong>Fecha:</strong> {createdAt}</p>
      <p><strong>Email cliente:</strong> {email}</p>
      <p><strong>Dirección de envío:</strong> {shippingAddress}</p>
      <p><strong>Dirección de facturación:</strong> {billingAddress}</p>
      <p><strong>Método de pago:</strong> {paymentMethod}</p>
      <p><strong>Estado del pago:</strong> {paymentStatus}</p>
      <h3>Artículos</h3>
      <ul>
        {items.map((item, idx) => (
          <li key={idx}>
            <strong>{item.productName}</strong> - Cantidad: {item.quantity}, Precio unitario: {item.unitPrice} €, Subtotal: {item.subtotal} €
          </li>
        ))}
      </ul>
    </div>
  );
}

export default OrderDetailsDisplay;
