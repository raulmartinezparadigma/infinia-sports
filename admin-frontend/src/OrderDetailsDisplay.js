// OrderDetailsDisplay.js
// Muestra los detalles relevantes de un pedido
import React, { useEffect, useState } from 'react';
import { fetchPaymentInfoByOrderId } from './api';

function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}

function OrderDetailsDisplay({ order, notFound }) {
  const TASA_IVA_TOTAL = 0.21; // Tasa de IVA para el total de la cesta. Ajustar si es necesario.
  // Estado local para método y estado de pago (movido al inicio)
  const [paymentInfo, setPaymentInfo] = useState({ method: '', status: '' });

  useEffect(() => {
    async function loadPaymentInfo() {
      // Asegurarse de que 'order' y 'order.id' existen antes de usarlos
      if (order && order.id) {
        const info = await fetchPaymentInfoByOrderId(order.id);
        // Si no se encuentra info o es null, usar valores por defecto para evitar errores
        setPaymentInfo(info || { method: '', status: '' });
      } else {
        // Si no hay order o order.id, resetear paymentInfo a valores por defecto
        setPaymentInfo({ method: '', status: '' });
      }
    }
    loadPaymentInfo();
  }, [order?.id]); // Depender de order?.id para re-ejecutar el efecto si order cambia

  if (notFound) {
    return (
      <div className="order-details-display">
        <h2>Pedido no encontrado</h2>
        <p>Verifica el Id del pedido e inténtalo de nuevo.</p>
      </div>
    );
  }
  if (!order) return null;

  // Extracción de datos principales
  const {
    id,
    status,
    priceInfo = {},
    submitDate,
    email,
    shippingAddress = {},
    billingAddress = {},
    payment = {},
    shippingGroups = []
  } = order;

  // Artículos
  const lineItems = (shippingGroups[0] && shippingGroups[0].lineItems) || [];

  // Nombre del cliente
  const nombreCliente = [shippingAddress.firstName, shippingAddress.lastName].filter(Boolean).join(' ');

  // Estado del pago formateado
  const pagoEstado = paymentInfo.status === 'COMPLETED' ? 'Pagado' : paymentInfo.status;

  return (
    <div className="order-details-display">
      <h2>Detalles del Pedido</h2>
      <p><strong>ID:</strong> {id}</p>
      <p><strong>Estado:</strong> {status}</p>
      <p><strong>Importe:</strong> {priceInfo.total} €</p>
      <p><strong>Fecha:</strong> {formatDate(submitDate)}</p>
      <p><strong>Email cliente:</strong> {email}</p>
      <p><strong>Dirección de envío:</strong> {shippingAddress.addressLine1}</p>
      <p><strong>Nombre del cliente:</strong> {nombreCliente}</p>
      <p><strong>Localidad:</strong> {shippingAddress.city}</p>
      <p><strong>Provincia:</strong> {shippingAddress.state}</p>
      <p><strong>Teléfono:</strong> {shippingAddress.phoneNumber}</p>
      {billingAddress && billingAddress.addressLine1 && (
        <>
          <p><strong>Dirección de facturación:</strong> {billingAddress.addressLine1}</p>
          <p><strong>Nombre del cliente (facturación):</strong> {[billingAddress.firstName, billingAddress.lastName].filter(Boolean).join(' ')}</p>
          <p><strong>Localidad (facturación):</strong> {billingAddress.city}</p>
          <p><strong>Provincia (facturación):</strong> {billingAddress.state}</p>
          <p><strong>Teléfono (facturación):</strong> {billingAddress.phoneNumber}</p>
        </>
      )}
      <p><strong>Método de pago:</strong> {paymentInfo.method}</p>
      <p><strong>Estado del pago:</strong> {pagoEstado}</p>
      <h3>Artículos</h3>
      {lineItems && lineItems.length > 0 ? (
        <table className="articles-table" style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
          <thead>
            <tr>
              <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'left' }}>Foto</th>
              <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'left' }}>Nombre</th>
              <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right' }}>Cantidad</th>
              <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right' }}>Precio por artículo</th>
            </tr>
          </thead>
          <tbody>
            {lineItems.map((item, idx) => (
              <tr key={idx}>
                <td style={{ border: '1px solid #ddd', padding: '8px' }}>
                  {item.productImageUrl || item.product?.imageUrl ? (
                    <img
                      src={
                        (item.productImageUrl || item.product?.imageUrl).startsWith('http')
                          ? (item.productImageUrl || item.product?.imageUrl)
                          : `http://localhost:3000/${item.productImageUrl || item.product?.imageUrl}`
                      }
                      alt={item.productName}
                      style={{ width: '50px', height: '50px', objectFit: 'cover' }}
                      onError={e => { e.target.onerror = null; e.target.src = 'https://via.placeholder.com/50x50?text=No+img'; }}
                    />
                  ) : 'No disponible'}
                </td>
                <td style={{ border: '1px solid #ddd', padding: '8px' }}>{item.productName}</td>
                <td style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right' }}>{item.quantity}</td>
                <td style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right' }}>{item.unitPrice?.toFixed(2)} €</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr>
              <td colSpan="3" style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>Subtotal (Base Imponible):</td>
              <td style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>
                {priceInfo.subtotal ? priceInfo.subtotal?.toFixed(2) : ((priceInfo.total || 0) / (1 + TASA_IVA_TOTAL))?.toFixed(2)} €
              </td>
            </tr>
            <tr>
              <td colSpan="3" style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>IVA ({ (TASA_IVA_TOTAL * 100).toFixed(0) }%):</td>
              <td style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>
                {priceInfo.tax ? priceInfo.tax?.toFixed(2) : ((priceInfo.total || 0) - ((priceInfo.total || 0) / (1 + TASA_IVA_TOTAL)))?.toFixed(2)} €
              </td>
            </tr>
            <tr>
              <td colSpan="3" style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>Total Pedido:</td>
              <td style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{priceInfo.total?.toFixed(2)} €</td>
            </tr>
          </tfoot>
        </table>
      ) : (
        <p>No hay artículos en este pedido.</p>
      )}

    </div>
  );
}


export default OrderDetailsDisplay;
