import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Typography, Paper, Checkbox, FormControlLabel, Radio, RadioGroup, FormControl, FormLabel, CircularProgress, Alert } from '@mui/material';
import BackgroundCarousel from './BackgroundCarousel';
import { getCurrentUser } from '../authApi'; // Reutilizamos la función

function BillingForm({ onNext, onBack, isAnonymous = false }) {
  const [values, setValues] = useState(() => {
    const stored = localStorage.getItem('billingAddress');
    return stored ? JSON.parse(stored) : {
      fullName: "",
      address: "",
      city: "",
      postalCode: "",
      province: "",
      country: "",
      nif: ""
    };
  });

  const [errors, setErrors] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [useSameAsShipping, setUseSameAsShipping] = useState(false);
  const [isLoading, setIsLoading] = useState(!isAnonymous);
  const [view, setView] = useState('list'); // 'list' o 'form'
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState(null);

  // Cargar direcciones del usuario
  useEffect(() => {
    if (isAnonymous) {
      setView('form');
      setIsLoading(false);
      return;
    }

    const fetchUserData = async () => {
      try {
        const userData = await getCurrentUser();
        if (userData.addresses && userData.addresses.length > 0) {
          setAddresses(userData.addresses);
          // No pre-seleccionamos ninguna para que el usuario elija activamente
          setView('list');
        } else {
          setView('form');
        }
      } catch (error) {
        console.error("Error al cargar las direcciones del usuario", error);
        setErrors({ general: 'No se pudieron cargar tus direcciones.' });
        setView('form');
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, [isAnonymous]);

  // Efecto para manejar la opción "Usar misma dirección que envío"
  useEffect(() => {
    if (useSameAsShipping) {
      const shippingAddress = localStorage.getItem('shippingAddress');
      if (shippingAddress) {
        const parsed = JSON.parse(shippingAddress);
        // Adaptar los campos al formulario de facturación
        const billingValues = {
          fullName: parsed.fullName || '',
          address: parsed.address || '',
          city: parsed.city || '',
          postalCode: parsed.postalCode || '',
          province: parsed.province || '',
          country: parsed.country || '',
          nif: values.nif || '' // Mantenemos el NIF si ya se había introducido
        };
        setValues(billingValues);
        localStorage.setItem('billingAddress', JSON.stringify(billingValues));
      }
    }
  }, [useSameAsShipping, values.nif]);

  const validate = () => {
    const errs = {};
    if (!values.fullName) errs.fullName = "Obligatorio";
    if (!values.address) errs.address = "Obligatorio";
    if (!values.city) errs.city = "Obligatorio";
    if (!values.postalCode) errs.postalCode = "Obligatorio";
    if (!/^\d{5}$/.test(values.postalCode)) errs.postalCode = "Código postal inválido";
    if (!values.province) errs.province = "Obligatorio";
    if (!values.country) errs.country = "Obligatorio";
    if (!values.nif) errs.nif = "Obligatorio";
    if (!/^([0-9]{8}[A-Z]|[A-Z][0-9]{7}[A-Z0-9])$/.test(values.nif)) errs.nif = "NIF/CIF inválido";
    return errs;
  };

  const handleChange = e => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleAddressSelectionChange = (event) => {
    setSelectedAddress(event.target.value);
  };

  const handleContinueWithSelectedAddress = () => {
    const addressId = parseInt(selectedAddress, 10);
    const address = addresses.find(a => a.id === addressId);
    if (address) {
      const billingValues = {
        fullName: `${address.firstName} ${address.lastName}`.trim(),
        address: address.addressLine1,
        city: address.city,
        postalCode: address.postalCode,
        province: address.state,
        country: address.country,
        nif: values.nif || '' // Mantener el NIF si se introdujo
      };
      localStorage.setItem('billingAddress', JSON.stringify(billingValues));
      if (onNext) onNext();
    }
  };

  const handleSubmit = e => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length === 0) {
      setSubmitted(true);
      localStorage.setItem("billingAddress", JSON.stringify(values));
      if (onNext) {
        onNext();
      }
      setTimeout(() => setSubmitted(false), 2500);
    }
  };

  const renderAddress = (addr) => (
    <Box key={addr.id} sx={{ mb: 1 }}>
      <Typography variant="body1" fontWeight="bold">{addr.firstName} {addr.lastName}</Typography>
      <Typography variant="body2">{addr.addressLine1}</Typography>
      {addr.addressLine2 && <Typography variant="body2">{addr.addressLine2}</Typography>}
      <Typography variant="body2">{addr.postalCode}, {addr.city}, {addr.state}</Typography>
      <Typography variant="body2">{addr.country}</Typography>
    </Box>
  );

  return (
    <Box sx={{ position: 'relative', mt: 4, mb: 4, minHeight: 500 }}>
      <BackgroundCarousel borderRadius={4} minHeight={500} />
      <Paper elevation={2} sx={{ p: 3, maxWidth: 500, margin: '32px auto', position: 'relative', zIndex: 2, borderRadius: 4, backdropFilter: 'blur(0.5px)' }}>
        <Typography variant="h6" gutterBottom>Dirección de facturación</Typography>

        {!isAnonymous && (
          <FormControlLabel
            control={<Checkbox checked={useSameAsShipping} onChange={(e) => setUseSameAsShipping(e.target.checked)} />}
            label="La dirección de facturación es la misma que la de envío"
            sx={{ mb: 2 }}
          />
        )}

        {useSameAsShipping ? (
          <Box>
            <Alert severity="info" sx={{ mb: 2 }}>La dirección de envío se usará para la facturación.</Alert>
            <TextField
                label="NIF/CIF"
                name="nif"
                value={values.nif}
                onChange={handleChange}
                error={!!errors.nif}
                helperText={errors.nif}
                fullWidth
                margin="normal"
                required
              />
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              <Button variant="text" onClick={onBack}>&larr; Volver</Button>
              <Button variant="contained" onClick={() => { if(validate().nif) { setErrors(validate()) } else if(onNext) onNext() } } >Continuar</Button>
            </Box>
          </Box>
        ) : isLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}><CircularProgress /></Box>
        ) : view === 'list' && !isAnonymous ? (
          // VISTA DE LISTA
          <Box>
            <FormControl component="fieldset" fullWidth>
              <FormLabel component="legend">Selecciona una dirección de facturación</FormLabel>
              <RadioGroup value={selectedAddress} onChange={handleAddressSelectionChange}>
                {addresses.map((addr) => (
                  <FormControlLabel 
                    key={addr.id} 
                    value={addr.id.toString()} 
                    control={<Radio />} 
                    label={renderAddress(addr)}
                    sx={{ alignItems: 'flex-start', border: '1px solid #ddd', borderRadius: 2, p: 1, mb: 1, '&.Mui-focused': { borderColor: 'primary.main' }}}
                  />
                ))}
              </RadioGroup>
            </FormControl>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              <Button variant="outlined" onClick={() => setView('form') }>Añadir nueva</Button>
              <Button variant="contained" onClick={handleContinueWithSelectedAddress} disabled={!selectedAddress}>Usar esta dirección</Button>
            </Box>
            <Button variant="text" onClick={onBack} sx={{ mt: 2 }}>&larr; Volver</Button>
          </Box>
        ) : (
          // VISTA DE FORMULARIO
          <form onSubmit={handleSubmit}>
            <TextField
              label="Nombre completo"
              name="fullName"
              value={values.fullName}
              onChange={handleChange}
              error={!!errors.fullName}
              helperText={errors.fullName}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Dirección"
              name="address"
              value={values.address}
              onChange={handleChange}
              error={!!errors.address}
              helperText={errors.address}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Ciudad"
              name="city"
              value={values.city}
              onChange={handleChange}
              error={!!errors.city}
              helperText={errors.city}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Código postal"
              name="postalCode"
              value={values.postalCode}
              onChange={handleChange}
              error={!!errors.postalCode}
              helperText={errors.postalCode}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Provincia"
              name="province"
              value={values.province}
              onChange={handleChange}
              error={!!errors.province}
              helperText={errors.province}
              fullWidth
              margin="normal"
            />
            <TextField
              label="País"
              name="country"
              value={values.country}
              onChange={handleChange}
              error={!!errors.country}
              helperText={errors.country}
              fullWidth
              margin="normal"
            />
            <TextField
              label="NIF/CIF"
              name="nif"
              value={values.nif}
              onChange={handleChange}
              error={!!errors.nif}
              helperText={errors.nif}
              fullWidth
              margin="normal"
              required
            />
            {errors.general && <Alert severity="error" sx={{ mt: 2 }}>{errors.general}</Alert>}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              {addresses.length > 0 && !isAnonymous ? (
                  <Button variant="outlined" onClick={() => setView('list') }>Cancelar</Button>
              ) : (
                  <Button variant="text" onClick={onBack}>&larr; Volver</Button>
              )}
              <Button type="submit" variant="contained" disabled={submitted}>Guardar y continuar</Button>
            </Box>
          </form>
        )}
      </Paper>
    </Box>
  );
}

export default BillingForm;
