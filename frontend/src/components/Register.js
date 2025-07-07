import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "./AuthContext";
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  Alert,
  Paper,
  CircularProgress
} from "@mui/material";

function Register() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    addressLine1: "",
    addressLine2: "",
    city: "",
    state: "",
    postalCode: "",
    country: "",
    phoneNumber: "",
    nif: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setFieldErrors({});

    // Validación básica de contraseña
    if (formData.password !== formData.confirmPassword) {
      setError("Las contraseñas no coinciden");
      setLoading(false);
      return;
    }

    // Validación de campos obligatorios
    const requiredFields = [
      "username", "email", "password", "confirmPassword",
      "firstName", "lastName", "addressLine1", "city", "state", "postalCode", "country", "phoneNumber", "nif"
    ];
    const newFieldErrors = {};
    requiredFields.forEach(field => {
      if (!formData[field]) {
        newFieldErrors[field] = "Obligatorio";
      }
    });
    if (Object.keys(newFieldErrors).length > 0) {
      setFieldErrors(newFieldErrors);
      setError("Por favor, completa todos los campos obligatorios.");
      setLoading(false);
      return;
    }

    try {
      const userData = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        addressLine1: formData.addressLine1,
        addressLine2: formData.addressLine2,
        city: formData.city,
        state: formData.state,
        postalCode: formData.postalCode,
        country: formData.country,
        phoneNumber: formData.phoneNumber,
        nif: formData.nif
      };
      await register(userData);
      navigate("/");
    } catch (err) {
      if (err.response?.data) {
        if (typeof err.response.data === 'object' && !Array.isArray(err.response.data)) {
          setFieldErrors(err.response.data);
          setError(err.response.data.message || "Error al registrar usuario");
        } else {
          setError(err.response.data.message || "Error al registrar usuario");
        }
      } else {
        setError("Error al registrar usuario");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box mt={6}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h5" align="center" gutterBottom>
            Crear cuenta
          </Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <form onSubmit={handleSubmit} noValidate>
            <TextField
              label="Usuario"
              name="username"
              value={formData.username}
              onChange={handleChange}
              error={!!fieldErrors.username}
              helperText={fieldErrors.username}
              fullWidth
              margin="dense"
              autoFocus
            />
            <TextField
              label="Email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              error={!!fieldErrors.email}
              helperText={fieldErrors.email}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Contraseña"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              error={!!fieldErrors.password}
              helperText={fieldErrors.password}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Confirmar contraseña"
              name="confirmPassword"
              type="password"
              value={formData.confirmPassword}
              onChange={handleChange}
              error={!!fieldErrors.confirmPassword}
              helperText={fieldErrors.confirmPassword}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Nombre"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              error={!!fieldErrors.firstName}
              helperText={fieldErrors.firstName}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Apellidos"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              error={!!fieldErrors.lastName}
              helperText={fieldErrors.lastName}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Dirección"
              name="addressLine1"
              value={formData.addressLine1}
              onChange={handleChange}
              error={!!fieldErrors.addressLine1}
              helperText={fieldErrors.addressLine1}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Dirección adicional (opcional)"
              name="addressLine2"
              value={formData.addressLine2}
              onChange={handleChange}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Ciudad"
              name="city"
              value={formData.city}
              onChange={handleChange}
              error={!!fieldErrors.city}
              helperText={fieldErrors.city}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Provincia"
              name="state"
              value={formData.state}
              onChange={handleChange}
              error={!!fieldErrors.state}
              helperText={fieldErrors.state}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Código postal"
              name="postalCode"
              value={formData.postalCode}
              onChange={handleChange}
              error={!!fieldErrors.postalCode}
              helperText={fieldErrors.postalCode}
              fullWidth
              margin="dense"
            />
            <TextField
              label="País"
              name="country"
              value={formData.country}
              onChange={handleChange}
              error={!!fieldErrors.country}
              helperText={fieldErrors.country}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Teléfono"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              error={!!fieldErrors.phoneNumber}
              helperText={fieldErrors.phoneNumber}
              fullWidth
              margin="dense"
            />
            <TextField
              label="NIF"
              name="nif"
              value={formData.nif}
              onChange={handleChange}
              error={!!fieldErrors.nif}
              helperText={fieldErrors.nif}
              fullWidth
              margin="dense"
            />
            <Box mt={2}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} /> : "Registrarse"}
              </Button>
            </Box>
          </form>
          <Box mt={2} textAlign="center">
            <Typography variant="body2">
              ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

export default Register;
