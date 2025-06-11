import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ProductCard from './ProductCard';

// Mock del contexto del carrito
jest.mock('./CartContext', () => ({
  useCart: () => ({
    addToCart: jest.fn(),
  }),
}));

describe('ProductCard', () => {
  const mockProduct = {
    id: '1',
    name: 'Zapatillas Running',
    description: 'Zapatillas para correr de alta calidad',
    price: 89.99,
    size: '42',
    type: 'Calzado'
  };

  beforeEach(() => {
    // Limpiar todos los mocks antes de cada test
    jest.clearAllMocks();
  });

  it('renderiza correctamente la información del producto', () => {
    render(<ProductCard product={mockProduct} />);
    
    // Verificar que se muestra la información del producto
    expect(screen.getByText('Zapatillas Running')).toBeInTheDocument();
    expect(screen.getByText('Zapatillas para correr de alta calidad')).toBeInTheDocument();
    expect(screen.getByText('Tipo: Calzado')).toBeInTheDocument();
    expect(screen.getByText('Talla/Peso: 42')).toBeInTheDocument();
    expect(screen.getByText('89.99 €')).toBeInTheDocument();
    expect(screen.getByText('Añadir al carrito')).toBeInTheDocument();
  });

  it('muestra imagen por defecto cuando no hay imagen del producto', () => {
    render(<ProductCard product={mockProduct} />);
    
    const img = screen.getByAltText('Zapatillas para correr de alta calidad');
    expect(img).toBeInTheDocument();
    
    // Simular error de carga de imagen
    fireEvent.error(img);
    
    // Verificar que se usa la imagen por defecto
    expect(img.src).toContain('logo512.png');
  });

  it('muestra Snackbar al añadir producto al carrito', async () => {
    render(<ProductCard product={mockProduct} />);
    
    // Hacer clic en el botón "Añadir al carrito"
    fireEvent.click(screen.getByText('Añadir al carrito'));
    
    // Verificar que aparece el Snackbar con el mensaje
    expect(screen.getByText('Producto añadido a la cesta')).toBeInTheDocument();
  });
});
