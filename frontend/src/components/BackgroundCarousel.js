import React, { useState, useEffect, useRef } from "react";
import Box from "@mui/material/Box";
import dumbbellImg from '../assets/dumbbell.jpg';
import proteinShakeImg from '../assets/protein_shake.jpg';
import manImg from '../assets/man.jpeg';
import runImg from '../assets/run.jpeg';

// Carrusel de fondo para formularios de checkout
// Muestra imágenes de fondo con transición automática y un degradado oscuro encima
function BackgroundCarousel({ borderRadius = 4, minHeight = 500 }) {
  // Imágenes a rotar
  const images = [dumbbellImg, proteinShakeImg, manImg, runImg];
  const [index, setIndex] = useState(0);
  const timeoutRef = useRef();

  useEffect(() => {
    timeoutRef.current = setTimeout(() => {
      setIndex((prev) => (prev + 1) % images.length);
    }, 3500);
    return () => clearTimeout(timeoutRef.current);
  }, [index, images.length]);

  return (
    <Box sx={{
      position: 'absolute',
      inset: 0,
      zIndex: 0,
      minHeight,
      width: '100%',
      height: '100%',
      overflow: 'hidden',
      borderRadius
    }}>
      <img
        src={images[index]}
        alt={`Fondo ${index + 1}`}
        style={{ width: '100%', height: '100%', objectFit: 'cover', transition: 'opacity 0.8s', opacity: 0.9, position: 'absolute', left: 0, top: 0 }}
      />
      {/* Degradado oscuro para resaltar el contenido */}
      <Box sx={{
        position: 'absolute',
        inset: 0,
        background: 'linear-gradient(180deg, rgba(15,20,40,0.36) 0%, rgba(30,30,35,0.24) 100%)',
        zIndex: 1,
        borderRadius
      }} />
    </Box>
  );
}

export default BackgroundCarousel;
