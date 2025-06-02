import React from "react";

// Formulario de dirección de envío
import { useState } from "react";
import { Box, TextField, Button, Typography, Paper } from "@mui/material";
import BackgroundCarousel from './BackgroundCarousel';

function ShippingForm({ onNext }) {
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

  const handleSubmit = e => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length === 0) {
      setSubmitted(true);
      localStorage.setItem("shippingAddress", JSON.stringify(values));
      if (onNext) {
        onNext();
      }
      setTimeout(() => setSubmitted(false), 2500);
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
