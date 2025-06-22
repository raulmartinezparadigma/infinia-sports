import React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import SportsScoreIcon from '@mui/icons-material/SportsScore';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PaymentIcon from '@mui/icons-material/Payment';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const steps = [
  { label: 'Carrito', icon: <SportsScoreIcon sx={{ color: '#1976d2' }} /> },
  { label: 'Envío', icon: <LocalShippingIcon sx={{ color: '#1976d2' }} /> },
  { label: 'Pago', icon: <PaymentIcon sx={{ color: '#1976d2' }} /> },
  { label: 'Confirmación', icon: <CheckCircleIcon sx={{ color: '#1976d2' }} /> },
];

export default function CheckoutStepperBar({ step }) {
  return (
    <Box sx={{ width: '100%', maxWidth: 600, mx: 'auto', py: 1 }}>
      <Stepper activeStep={step} alternativeLabel>
        {steps.map((stepObj, index) => (
          <Step key={stepObj.label} completed={step > index}>
            <StepLabel
              icon={React.cloneElement(stepObj.icon, {
                sx: {
                  color: step === index ? '#006F62' : '#1976d2',
                  fontWeight: step === index ? 700 : 400,
                }
              })}
              sx={{
                '& .MuiStepLabel-label': {
                  color: step === index ? '#006F62' : 'inherit',
                  fontWeight: step === index ? 700 : 400,
                }
              }}
            >
              {stepObj.label}
            </StepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  );
}
