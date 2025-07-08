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

// Componente de inicio de sesión
function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  
  const { login } = useAuth();
  const navigate = useNavigate();
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setFieldErrors({});
    
    try {
      await login(username, password);
      // Redireccionar a la página principal después del login exitoso
      navigate("/");
    } catch (err) {
      if (err.response?.data) {
        if (typeof err.response.data === 'object' && !Array.isArray(err.response.data)) {
          setFieldErrors(err.response.data);
          setError(err.response.data.message || "Error al iniciar sesión");
        } else {
          setError(err.response.data.message || "Error al iniciar sesión");
        }
      } else {
        setError("Error al iniciar sesión");
      }
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom align="center">
            Iniciar Sesión
          </Typography>
          
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {/* Mostrar errores de validación de campos */}
          {Object.keys(fieldErrors).length > 0 && (
            <Box sx={{ mb: 2 }}>
              {Object.entries(fieldErrors).map(([field, msg]) =>
                field !== 'message' ? (
                  <Alert key={field} severity="error" sx={{ mb: 1 }}>
                    {field.charAt(0).toUpperCase() + field.slice(1)}: {msg}
                  </Alert>
                ) : null
              )}
            </Box>
          )}
          <Box component="form" onSubmit={handleSubmit} noValidate>
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="Nombre de usuario"
              name="username"
              autoComplete="username"
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
              error={Boolean(fieldErrors.username)}
              helperText={fieldErrors.username}
              inputProps={{ 'data-testid': 'username-input' }}
            />
            
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="Contraseña"
              type="password"
              id="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
              error={Boolean(fieldErrors.password)}
              helperText={fieldErrors.password}
              inputProps={{ 'data-testid': 'password-input' }}
            />
            
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
              data-testid="submit-login-button"
            >
              {loading ? <CircularProgress size={24} /> : "Iniciar Sesión"}
            </Button>
            
            <Box sx={{ textAlign: "center", mt: 2 }}>
              <Typography variant="body2">
                ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
              </Typography>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

export default Login;
