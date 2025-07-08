import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Typography, Paper, Alert, Radio, RadioGroup, FormControl, FormControlLabel, FormLabel, CircularProgress } from '@mui/material';
import BackgroundCarousel from './BackgroundCarousel';

import { saveShippingAddress } from '../cartApi';
import { useCart } from './CartContext';
import { getCurrentUser, addUserAddress } from '../authApi';

function ShippingForm({ onNext, onBack, isAnonymous = false }) {
  const [values, setValues] = useState(() => {
    const stored = localStorage.getItem('shippingAddress');
    return stored ? JSON.parse(stored) : {
      fullName: "",
      address: "",
      city: "",
      postalCode: "",
      province: "",
      country: "",
      phone: "",
      email: ""
    };
  });

  const [errors, setErrors] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [isLoading, setIsLoading] = useState(!isAnonymous);
  const [view, setView] = useState('list'); // 'list' o 'form'
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [userEmail, setUserEmail] = useState('');

  const { cartId } = useCart();

  useEffect(() => {
    if (isAnonymous) {
      setView('form');
      setIsLoading(false);
      return;
    }

    const fetchUserData = async () => {
      try {
        const userData = await getCurrentUser();
        setUserEmail(userData.email); // Guardar el email del usuario
        if (userData.addresses && userData.addresses.length > 0) {
          setAddresses(userData.addresses);
          setSelectedAddress(userData.addresses[0].id.toString()); // Seleccionar la primera por defecto
          setView('list');
        } else {
          // Si no hay direcciones, ir directamente al formulario
          setView('form');
        }
      } catch (error) {
        console.error("Error al cargar las direcciones del usuario", error);
        setErrors({ general: 'No se pudieron cargar tus direcciones.' });
        setView('form'); // Si hay error, mostrar formulario para no bloquear
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, [isAnonymous]);

  const validate = () => {
    const errs = {};
    if (!values.fullName) errs.fullName = "Obligatorio";
    if (!values.address) errs.address = "Obligatorio";
    if (!values.city) errs.city = "Obligatorio";
    if (!values.postalCode) errs.postalCode = "Obligatorio";
    if (!/^\d{5}$/.test(values.postalCode)) errs.postalCode = "Código postal inválido";
    if (!values.province) errs.province = "Obligatorio";
    if (!values.country) errs.country = "Obligatorio";
    if (!values.phone) errs.phone = "Obligatorio";
    if (!/^\d{9}$/.test(values.phone)) errs.phone = "Teléfono inválido";
    if (!values.email) errs.email = "Obligatorio";
    if (!/^\S+@\S+\.\S+$/.test(values.email)) errs.email = "Email inválido";
    return errs;
  };

  const handleChange = e => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleAddressSelectionChange = (event) => {
    setSelectedAddress(event.target.value);
  };

  // Procede al siguiente paso usando la dirección seleccionada
  const handleContinueWithSelectedAddress = async () => {
    const addressId = parseInt(selectedAddress, 10);
    const address = addresses.find(a => a.id === addressId);

    if (!address) {
      setErrors({ general: 'Por favor, selecciona una dirección válida.' });
      return;
    }

    // Mapear AddressDTO a lo que espera saveShippingAddress
    const shippingAddressForCart = {
      firstName: address.firstName,
      lastName: address.lastName,
      addressLine1: address.addressLine1,
      addressLine2: address.addressLine2,
      city: address.city,
      state: address.state,
      postalCode: address.postalCode,
      country: address.country,
      phoneNumber: address.phoneNumber,
      email: userEmail, // Usar el email del usuario guardado en el estado
    };

    // Guardar en localStorage para consistencia con flujo anterior
    const formValues = {
      fullName: `${address.firstName} ${address.lastName}`.trim(),
      address: address.addressLine1,
      city: address.city,
      postalCode: address.postalCode,
      province: address.state,
      country: address.country,
      phone: address.phoneNumber,
      email: userEmail // Usar el email del usuario aquí también
    };
    localStorage.setItem("shippingAddress", JSON.stringify(formValues));

    try {
      console.log('[ShippingForm] Guardando dirección seleccionada en el carrito:', { cartId, shippingAddressForCart });
      await saveShippingAddress(cartId, shippingAddressForCart, true);
      if (onNext) {
        onNext();
      }
    } catch (error) {
      setErrors({ general: 'Error al guardar la dirección en el carrito. Inténtalo de nuevo.' });
      console.error('[ShippingForm] Error al guardar dirección seleccionada:', error);
    }
  };

  // Guarda una nueva dirección (desde el formulario)
  const handleSubmit = async e => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);

    if (Object.keys(errs).length === 0) {
      setSubmitted(true);

      let firstName = "";
      let lastName = "";
      if (values.fullName) {
        const split = values.fullName.trim().split(" ");
        firstName = split.shift() || "";
        lastName = split.join(" ") || "";
      }

      const addressDTO = {
        firstName,
        lastName,
        addressLine1: values.address,
        addressLine2: values.addressLine2 || "",
        city: values.city,
        state: values.province || values.state || "",
        postalCode: values.postalCode,
        country: values.country,
        phoneNumber: values.phone || values.phoneNumber || "",
        email: values.email
      };

      try {
        // 1. Guardar la dirección en el perfil del usuario (si no es anónimo)
        if (!isAnonymous) {
          const savedAddress = await addUserAddress(addressDTO);
          // Actualizar la lista de direcciones localmente
          const newAddresses = [...addresses, savedAddress];
          setAddresses(newAddresses);
          setSelectedAddress(savedAddress.id.toString());
        }

        // 2. Guardar la dirección en el carrito para este pedido
        console.log('[ShippingForm] AddressDTO enviado a saveShippingAddress:', addressDTO);
        await saveShippingAddress(cartId, addressDTO, true);
        localStorage.setItem("shippingAddress", JSON.stringify(values));

        // 3. Si no es anónimo, volver a la lista. Si es anónimo, ir al siguiente paso.
        if (!isAnonymous) {
          setView('list');
        } else if (onNext) {
          onNext();
        }

      } catch (error) {
        setErrors({ general: 'Error al guardar la dirección. Inténtalo de nuevo.' });
        console.error('[ShippingForm] Error al guardar dirección:', error);
      } finally {
        setTimeout(() => setSubmitted(false), 2500);
      }
    }
  };

  const renderAddress = (addr) => (
    <Box key={addr.id} sx={{ mb: 1 }}>
      <Typography variant="body1" fontWeight="bold">{addr.firstName} {addr.lastName}</Typography>
      <Typography variant="body2">{addr.addressLine1}</Typography>
      {addr.addressLine2 && <Typography variant="body2">{addr.addressLine2}</Typography>}
      <Typography variant="body2">{addr.postalCode}, {addr.city}, {addr.state}</Typography>
      <Typography variant="body2">{addr.country}</Typography>
      <Typography variant="body2">Teléfono: {addr.phoneNumber}</Typography>
    </Box>
  );

  return (
    <Box sx={{ position: 'relative', mt: '25px', mb: '25px', minHeight: 500 }}>
      <BackgroundCarousel borderRadius={4} minHeight={500} />
      <Paper elevation={2} sx={{ p: 3, maxWidth: 500, margin: '8px auto', position: 'relative', zIndex: 2, borderRadius: 4, backdropFilter: 'blur(0.5px)' }}>
        <Typography variant="h6" gutterBottom>Dirección de envío</Typography>

        {isLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
          </Box>
        ) : view === 'list' && !isAnonymous ? (
          // VISTA DE LISTA DE DIRECCIONES
          <Box>
            <FormControl component="fieldset" fullWidth>
              <FormLabel component="legend">Selecciona una dirección</FormLabel>
              <RadioGroup
                aria-label="dirección de envío"
                name="shipping-address-selection"
                value={selectedAddress}
                onChange={handleAddressSelectionChange}
              >
                {addresses.map((addr) => (
                  <FormControlLabel 
                    key={addr.id} 
                    value={addr.id.toString()} 
                    control={<Radio />} 
                    label={renderAddress(addr)}
                    sx={{ 
                      alignItems: 'flex-start',
                      border: '1px solid #ddd',
                      borderRadius: 2,
                      p: 1,
                      mb: 1,
                      '&.Mui-focused': { borderColor: 'primary.main' }
                    }}
                  />
                ))}
              </RadioGroup>
            </FormControl>
            {errors.general && <Alert severity="error" sx={{ mt: 2 }}>{errors.general}</Alert>}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              <Button variant="outlined" onClick={() => setView('form') }>
                Añadir nueva dirección
              </Button>
              <Button variant="contained" onClick={handleContinueWithSelectedAddress} disabled={!selectedAddress}>
                Usar esta dirección
              </Button>
            </Box>
             <Button variant="text" onClick={onBack} sx={{ mt: 2 }}>
                &larr; Volver
            </Button>
          </Box>
        ) : (
          // VISTA DE FORMULARIO
          <Box>
            {isAnonymous && (
              <Alert severity="info" sx={{ mb: 2 }}>
                Estás realizando un checkout como usuario anónimo. Tus datos no se guardarán para futuras compras.
              </Alert>
            )}
            <Box component="form" onSubmit={handleSubmit} noValidate>
              <TextField
                label="Nombre completo"
                name="fullName"
                value={values.fullName}
                onChange={handleChange}
                error={!!errors.fullName}
                helperText={errors.fullName}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Dirección"
                name="address"
                value={values.address}
                onChange={handleChange}
                error={!!errors.address}
                helperText={errors.address}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Ciudad"
                name="city"
                value={values.city}
                onChange={handleChange}
                error={!!errors.city}
                helperText={errors.city}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Código postal"
                name="postalCode"
                value={values.postalCode}
                onChange={handleChange}
                error={!!errors.postalCode}
                helperText={errors.postalCode}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Provincia"
                name="province"
                value={values.province}
                onChange={handleChange}
                error={!!errors.province}
                helperText={errors.province}
                fullWidth
                margin="dense"
              />
              <TextField
                label="País"
                name="country"
                value={values.country}
                onChange={handleChange}
                error={!!errors.country}
                helperText={errors.country}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Teléfono"
                name="phone"
                value={values.phone}
                onChange={handleChange}
                error={!!errors.phone}
                helperText={errors.phone}
                fullWidth
                margin="dense"
              />
              <TextField
                label="Email"
                name="email"
                value={values.email}
                onChange={handleChange}
                error={!!errors.email}
                helperText={errors.email}
                fullWidth
                margin="dense"
              />
              {errors.general && <Alert severity="error" sx={{ mt: 2 }}>{errors.general}</Alert>}
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
                {addresses.length > 0 && !isAnonymous ? (
                  <Button variant="outlined" onClick={() => setView('list') }>
                    Cancelar
                  </Button>
                ) : (
                  <Button variant="text" onClick={onBack}>
                    &larr; Volver
                  </Button>
                )}
                <Button type="submit" variant="contained" disabled={submitted}>
                  Guardar dirección
                </Button>
              </Box>
            </Box>
          </Box>
        )}
      </Paper>
    </Box>
  );
}

export default ShippingForm;
