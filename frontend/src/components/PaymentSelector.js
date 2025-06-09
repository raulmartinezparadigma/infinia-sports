import React from "react";
import RedsysPayment from "./RedsysPayment";
// Selector de método de pago
import { useState } from "react";
import {
  Box, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  Typography, Grid, Paper, TextField, IconButton
} from "@mui/material";
import PaymentIcon from '@mui/icons-material/Payment';
import EuroIcon from '@mui/icons-material/Euro';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';

const paymentMethods = [
  {
    key: "bizum",
    label: "Bizum",
    icon: <PhoneIphoneIcon fontSize="large" color="primary" />,
    description: "Pago rápido con tu móvil."
  },
  {
    key: "redsys",
    label: "Redsys",
    icon: <PaymentIcon fontSize="large" color="success" />,
    description: "Tarjeta de crédito/débito."
  },
  {
    key: "transferencia",
    label: "Transferencia bancaria",
    icon: <EuroIcon fontSize="large" color="secondary" />,
    description: "Transferencia directa a nuestra cuenta."
  }
];

function PaymentSelector({ onNext, onBack }) {
  const [selected, setSelected] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [bizumPhone, setBizumPhone] = useState("");
  const [redsysData, setRedsysData] = useState({ card: "", expiry: "", cvv: "" });

  // Abrir modal al seleccionar método
  const handleSelect = (key) => {
    setSelected(key);
    setModalOpen(true);
  };

  // Cambiar método dentro del modal
  const handleChangeMethod = (key) => {
    setSelected(key);
  };

  // Cerrar modal y resetear
  const handleClose = () => {
    setModalOpen(false);
    setSelected(null);
    setBizumPhone("");
    setRedsysData({ card: "", expiry: "", cvv: "" });
  };

  // Renderiza el contenido del modal según método
  const renderModalContent = () => {
    if (selected === "bizum") {
      // Modal simplificado: solo botones Confirmar y Cancelar
      return null;
    }
    if (selected === "redsys") {
      // Mostrar el formulario Redsys real
      return (
        <Box>
          <RedsysPayment onSuccess={() => {
            setModalOpen(false);
            if (typeof onNext === 'function') onNext({ paymentMethod: 'redsys' });
          }} />
        </Box>
      );
    }
    if (selected === "transferencia") {
      return (
        <Box>
          <Typography>Realiza una transferencia a la siguiente cuenta:</Typography>
          <Paper sx={{ p: 2, my: 2, backgroundColor: '#f5f5f5' }}>
            <Typography variant="body2"><b>Banco:</b> Banco Ejemplo</Typography>
            <Typography variant="body2"><b>IBAN:</b> ES12 3456 7890 1234 5678 9012</Typography>
            <Typography variant="body2"><b>Concepto:</b> Tu nombre y número de pedido</Typography>
            <Typography variant="body2"><b>Importe:</b> El total de tu compra</Typography>
          </Paper>
          <Typography>Cuando recibamos el pago, confirmaremos tu pedido por email.</Typography>
        </Box>
      );
    }
    return null;
  };

  return (
    <Box sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h6" align="center" gutterBottom>
        Selecciona tu método de pago
      </Typography>
      <Grid container spacing={3} justifyContent="center">
        {paymentMethods.map(method => (
          <Grid item xs={12} sm={4} key={method.key}>
            <Paper
              elevation={selected === method.key ? 6 : 2}
              sx={{
                p: 2,
                textAlign: 'center',
                cursor: 'pointer',
                border: selected === method.key ? '2px solid #1976d2' : '1px solid #ccc',
                backgroundColor: selected === method.key ? '#e3f2fd' : 'white',
                transition: 'all 0.2s',
                minHeight: 140
              }}
              onClick={() => handleSelect(method.key)}
            >
              <Box>{method.icon}</Box>
              <Typography variant="subtitle1" sx={{ mt: 1 }}>{method.label}</Typography>
              <Typography variant="body2" color="text.secondary">{method.description}</Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>
      <Dialog open={modalOpen} onClose={handleClose} maxWidth="xs" fullWidth>
        <DialogTitle>
          Método de pago: {paymentMethods.find(m => m.key === selected)?.label}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mb: 2 }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Puedes cambiar de método de pago seleccionando otra tarjeta abajo.
            </Typography>
            {renderModalContent()}
          </Box>
          <Grid container spacing={2} justifyContent="center">
            {paymentMethods.map(method => (
              <Grid item key={method.key}>
                <IconButton
                  onClick={() => handleChangeMethod(method.key)}
                  color={selected === method.key ? "primary" : "default"}
                >
                  {method.icon}
                </IconButton>
              </Grid>
            ))}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="secondary">Cancelar</Button>
          <Button onClick={() => {
            handleClose();
            if (typeof onNext === 'function') onNext();
          }} variant="contained" color="primary">Confirmar</Button>
        </DialogActions>
      </Dialog>
      {typeof onBack === 'function' && (
        <Box mt={4} display="flex" justifyContent="center">
          <Button variant="contained" color="secondary" size="large" startIcon={<span style={{fontSize:20}}>&larr;</span>} onClick={onBack}>
            Volver
          </Button>
        </Box>
      )}
    </Box>
  );
}

export default PaymentSelector;
