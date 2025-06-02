import React from "react";

// Formulario de dirección de facturación
import { useState } from "react";
import { Box, TextField, Button, Typography, Paper } from "@mui/material";

function BillingForm({ onNext }) {
  const [values, setValues] = useState({
    fullName: "",
    address: "",
    city: "",
    postalCode: "",
    province: "",
    country: "",
    nif: ""
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
    if (!values.nif) errs.nif = "Obligatorio";
    if (!/^([0-9]{8}[A-Z]|[A-Z][0-9]{7}[A-Z0-9])$/.test(values.nif)) errs.nif = "NIF/CIF inválido";
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
      localStorage.setItem("billingAddress", JSON.stringify(values));
      if (onNext) {
        onNext();
      }
      setTimeout(() => setSubmitted(false), 2500);
    }
  };

  return (
    <Paper elevation={2} sx={{ mt: 4, p: 3, maxWidth: 500, margin: '32px auto' }}>
      <Typography variant="h6" gutterBottom>Dirección de facturación</Typography>
      <Box component="form" onSubmit={handleSubmit} noValidate>
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
        />
        <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
          Guardar dirección de facturación
        </Button>
        {submitted && (
          <Typography color="success.main" sx={{ mt: 2 }}>
            ¡Dirección de facturación guardada correctamente!
          </Typography>
        )}
      </Box>
    </Paper>
  );
}

export default BillingForm;
