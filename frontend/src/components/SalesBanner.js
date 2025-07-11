import React from 'react';
import Slider from 'react-slick';
import { Box, Typography } from '@mui/material';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const bannerItems = [
  {
    productImage: '/nike_air_max_90.jpg',
    backgroundColor: '#0d47a1', // Azul oscuro
    textColor: '#ffffff',
    discount: '25% OFF',
    title: 'Zapatillas en Rebajas',
  },
  {
    productImage: '/nike_dri_fit_shirt.jpg',
    backgroundColor: '#b71c1c', // Rojo oscuro
    textColor: '#ffffff',
    discount: '40% OFF',
    title: 'Ropa Deportiva',
  },
  {
    productImage: '/adidas_ultraboost.jpg',
    backgroundColor: '#1565c0', // Azul más claro
    textColor: '#ffffff',
    discount: '30% OFF',
    title: '¡Nuevas Ofertas!',
  },
];

const SalesBanner = () => {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 4000,
    arrows: false,
  };

  return (
    <Box sx={{ mb: 4, '.slick-dots li button:before': { fontSize: '12px', color: 'primary.main' } }}>
      <Slider {...settings}>
        {bannerItems.map((item, index) => (
          <Box key={index} sx={{ position: 'relative', height: 250, backgroundColor: item.backgroundColor, borderRadius: 2, overflow: 'hidden', p: 4, display: 'flex !important', alignItems: 'center', justifyContent: 'space-between' }}>
            <Box sx={{ zIndex: 1, color: item.textColor }}>
              <Typography variant="h2" component="div" sx={{ fontWeight: 'bold' }}>
                {item.discount}
              </Typography>
              <Typography variant="h5">{item.title}</Typography>
            </Box>
            <Box
              component="img"
              src={item.productImage}
              alt={item.title}
              sx={{
                position: 'absolute',
                right: '10%',
                top: '50%',
                transform: 'translateY(-50%) rotate(15deg)',
                height: '150%',
                width: 'auto',
                opacity: 0.2,
                zIndex: 0,
              }}
            />
             <Box
              component="img"
              src={item.productImage}
              alt={item.title}
              sx={{
                position: 'relative',
                zIndex: 1,
                height: '120%',
                width: 'auto',
                maxWidth: '50%',
                transform: 'translateY(-5%)',
                filter: 'drop-shadow(0px 10px 15px rgba(0,0,0,0.3))',
              }}
            />
          </Box>
        ))}
      </Slider>
    </Box>
  );
};

export default SalesBanner;
