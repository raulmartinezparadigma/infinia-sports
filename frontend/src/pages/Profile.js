import React, { useState } from 'react';
import { useAuth } from '../components/AuthContext';
import { addUserAddress } from '../authApi';
import AddressForm from '../components/AddressForm';
import './Profile.css';

const Profile = () => {
  const { currentUser: user, setCurrentUser: setUser } = useAuth();
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [error, setError] = useState(null);

  const handleAddAddress = async (address) => {
    try {
      const newAddress = await addUserAddress(address);
      setUser(prevUser => ({
        ...prevUser,
        addresses: [...prevUser.addresses, newAddress]
      }));
      setShowAddressForm(false);
      setError(null);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Error desconocido al añadir la dirección.';
      setError(errorMessage);
      console.error('Error al añadir la dirección:', err);
    }
  };

  if (!user) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="profile-container">
      <h2>Mi Perfil</h2>
      <div className="profile-section">
        <h3>Datos de usuario</h3>
        <div className="profile-field"><strong>Usuario:</strong> {user.username}</div>
        <div className="profile-field"><strong>Email:</strong> {user.email}</div>
        <div className="profile-field"><strong>Nombre:</strong> {user.firstName} {user.lastName}</div>
        <div className="profile-field"><strong>NIF:</strong> {user.nif || 'No especificado'}</div>
      </div>

      <div className="profile-section">
        <h3>Mis Direcciones</h3>
        {user.addresses && user.addresses.length > 0 ? (
          <ul className="address-list">
            {user.addresses.map((address) => (
              <li key={address.id} className="address-item">
                <div><strong>{address.firstName} {address.lastName}</strong> {address.mainAddress && <span className="badge bg-primary ms-2">Principal</span>}</div>
                <div>{address.addressLine1}, {address.addressLine2}</div>
                <div>{address.city}, {address.state}, {address.postalCode}</div>
                <div>{address.country}</div>
                <div>Teléfono: {address.phoneNumber}</div>
              </li>
            ))}
          </ul>
        ) : (
          <p>No tienes direcciones guardadas.</p>
        )}

        {showAddressForm ? (
          <AddressForm onSave={handleAddAddress} onCancel={() => setShowAddressForm(false)} />
        ) : (
          <button className="add-address-btn" onClick={() => setShowAddressForm(true)}>
            Añadir nueva dirección
          </button>
        )}
      </div>

      {error && <div className="alert alert-danger mt-3">{error}</div>}
    </div>
  );
};

export default Profile;
