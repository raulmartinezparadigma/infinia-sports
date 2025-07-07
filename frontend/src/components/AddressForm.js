import React, { useState } from 'react';
import './AddressForm.css';

const AddressForm = ({ onSave, onCancel }) => {
  const [address, setAddress] = useState({
    firstName: '',
    lastName: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
    country: '',
    phoneNumber: '',
  });

  const handleChange = (e) => {
    setAddress({ ...address, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(address);
  };

  return (
    <form className="address-form" onSubmit={handleSubmit}>
      <h4>Nueva dirección</h4>
      <input name="firstName" placeholder="Nombre" value={address.firstName} onChange={handleChange} required />
      <input name="lastName" placeholder="Apellidos" value={address.lastName} onChange={handleChange} required />
      <input name="addressLine1" placeholder="Dirección" value={address.addressLine1} onChange={handleChange} required />
      <input name="addressLine2" placeholder="Dirección 2" value={address.addressLine2} onChange={handleChange} />
      <input name="city" placeholder="Ciudad" value={address.city} onChange={handleChange} required />
      <input name="state" placeholder="Provincia" value={address.state} onChange={handleChange} />
      <input name="postalCode" placeholder="Código Postal" value={address.postalCode} onChange={handleChange} required />
      <input name="country" placeholder="País" value={address.country} onChange={handleChange} required />
      <input name="phoneNumber" placeholder="Teléfono" value={address.phoneNumber} onChange={handleChange} />
      <div className="address-form-actions">
        <button type="submit">Guardar</button>
        <button type="button" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
};

export default AddressForm;
