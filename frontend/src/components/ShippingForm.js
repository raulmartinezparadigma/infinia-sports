import React from "react";

// Formulario de dirección de envío
import { useState } from "react";
import { Box, TextField, Button, Typography, Paper } from "@mui/material";
import BackgroundCarousel from './BackgroundCarousel';

import { saveShippingAddress } from '../cartApi';
import { useCart } from './CartContext';

function ShippingForm({ onNext, onBack }) {
  const [values, setValues] = useState({
    fullName: "",
    address: "",
    city: "",
    postalCode: "",
    province: "",
    country: "",
    phone: ""
  });
  const [errors, setErrors] = useState({});
  const [submitted, setSubmitted] = useState(false);

  // Validación simple
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
    return errs;
  };

  const handleChange = e => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const { cartId } = useCart();

  // Maneja el envío del formulario y la integración con backend
  const handleSubmit = async e => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length === 0) {
      // Log para depuración
      console.log('[ShippingForm] Submit:', { cartId, values });
      if (!cartId) {
        setErrors({ general: 'No se ha podido obtener el identificador del carrito.' });
        console.error('[ShippingForm] No cartId');
        return;
      }
      setSubmitted(true);
      try {
        // Llamada al backend para guardar dirección
        // Adaptar los campos del formulario al DTO esperado por el backend
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
          phoneNumber: values.phone || values.phoneNumber || ""
        };
        console.log('[ShippingForm] AddressDTO enviado:', addressDTO);
        const resp = await saveShippingAddress(cartId, addressDTO, true);
        console.log('[ShippingForm] Dirección guardada. Respuesta backend:', resp);
        localStorage.setItem("shippingAddress", JSON.stringify(values));
        if (onNext) {
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



  return (
    <Box sx={{ position: 'relative', mt: 4, mb: 4, minHeight: 500 }}>
      {/* Carrusel de fondo */}
      <BackgroundCarousel borderRadius={4} minHeight={500} />
      <Paper elevation={2} sx={{ p: 1, maxWidth: 500, margin: '8px auto', position: 'relative', zIndex: 2, borderRadius: 4, backdropFilter: 'blur(0.5px)' }}>
      <Typography variant="h6" gutterBottom>Dirección de envío</Typography>
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
        <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
          Guardar dirección
        </Button>
        {errors.general && (
          <Typography color="error" sx={{ mt: 2 }}>
            {errors.general}
          </Typography>
        )}
        {typeof onBack === 'function' && (
          <Box mt={2} display="flex" justifyContent="center">
            <Button variant="contained" color="secondary" size="large" startIcon={<span style={{fontSize:20}}>&larr;</span>} onClick={onBack}>
              Volver
            </Button>
          </Box>
        )}
        {submitted && (
          <Typography color="success.main" sx={{ mt: 2 }}>
            ¡Dirección guardada correctamente!
          </Typography>
        )}
      </Box>
    </Paper>
    </Box>
  );
}

export default ShippingForm;
