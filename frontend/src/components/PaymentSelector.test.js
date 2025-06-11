import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import PaymentSelector from './PaymentSelector';

// Mock de los módulos necesarios
jest.mock('./CartContext', () => ({
  useCart: () => ({
    cartId: 'mock-cart-id',
    clearCartAndReload: jest.fn().mockResolvedValue(undefined)
  })
}));

jest.mock('./RedsysPayment', () => {
  const RedsysPaymentMock = ({ onSuccess }) => (
    <div data-testid="redsys-payment">
      <button onClick={onSuccess}>Simular pago exitoso</button>
    </div>
  );
  return RedsysPaymentMock;
});

jest.mock('../transferApi', () => ({
  payByTransfer: jest.fn().mockResolvedValue({ success: true })
}));

describe('PaymentSelector', () => {
  const mockOnNext = jest.fn();
  const mockOnBack = jest.fn();
  const mockAmount = 99.99;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renderiza correctamente los métodos de pago disponibles', () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Verificar que se muestran los tres métodos de pago
    expect(screen.getByText('Bizum')).toBeInTheDocument();
    expect(screen.getByText('Redsys')).toBeInTheDocument();
    expect(screen.getByText('Transferencia bancaria')).toBeInTheDocument();
    
    // Verificar que se muestra el botón de volver
    expect(screen.getByText('Volver')).toBeInTheDocument();
  });

  it('muestra el diálogo al seleccionar un método de pago', () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Bizum
    fireEvent.click(screen.getByText('Bizum'));
    
    // Verificar que se abre el diálogo
    expect(screen.getByText('Método de pago: Bizum')).toBeInTheDocument();
    expect(screen.getByText('Confirmar')).toBeInTheDocument();
    expect(screen.getByText('Cancelar')).toBeInTheDocument();
  });

  it('permite cambiar entre métodos de pago en el diálogo', async () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Bizum
    await act(async () => {
      fireEvent.click(screen.getByText('Bizum'));
    });
    
    // Verificar que se muestra el diálogo de Bizum
    expect(screen.getByText('Método de pago: Bizum')).toBeInTheDocument();
    
    // Buscar los botones de métodos de pago dentro del diálogo
    const transferButton = screen.getByText('Transferencia bancaria');
    
    // Cambiar al método Transferencia
    await act(async () => {
      fireEvent.click(transferButton);
    });
    
    // Verificar que ahora se muestra la información de transferencia
    expect(screen.getByText('Realiza una transferencia a la siguiente cuenta:')).toBeInTheDocument();
    expect(screen.getByText(/IBAN:/)).toBeInTheDocument();
  });

  it('llama a onNext al confirmar el pago con Bizum', async () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Bizum
    await act(async () => {
      fireEvent.click(screen.getByText('Bizum'));
    });
    
    // Confirmar el pago
    await act(async () => {
      fireEvent.click(screen.getByText('Confirmar'));
    });
    
    // Verificar que se llamó a onNext
    expect(mockOnNext).toHaveBeenCalled();
  });

  it('llama a onNext al confirmar el pago con Redsys', async () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Redsys
    await act(async () => {
      fireEvent.click(screen.getByText('Redsys'));
    });
    
    // Simular pago exitoso con Redsys
    await act(async () => {
      fireEvent.click(screen.getByText('Simular pago exitoso'));
    });
    
    // Verificar que se llamó a onNext con el método correcto
    expect(mockOnNext).toHaveBeenCalledWith({ paymentMethod: 'redsys' });
  });

  it('procesa correctamente el pago por transferencia', async () => {
    // Configurar el mock para que clearCartAndReload sea llamado
    const mockClearCartAndReload = jest.fn().mockResolvedValue(undefined);
    jest.mock('./CartContext', () => ({
      useCart: () => ({
        cartId: 'mock-cart-id',
        clearCartAndReload: mockClearCartAndReload
      })
    }), { virtual: true });
    
    const { payByTransfer } = await import('../transferApi');
    
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Transferencia
    await act(async () => {
      fireEvent.click(screen.getByText('Transferencia bancaria'));
    });
    
    // Confirmar la transferencia
    await act(async () => {
      fireEvent.click(screen.getByText('He realizado la transferencia'));
    });
    
    // Verificar que se llamó a la API de transferencia
    expect(payByTransfer).toHaveBeenCalledWith({
      orderId: 'mock-cart-id',
      amount: mockAmount,
      titular: "Cliente"
    });
    
    // Verificar que se llamó a onNext con el método correcto
    expect(mockOnNext).toHaveBeenCalledWith({ paymentMethod: 'transferencia' });
  });

  it('cierra el diálogo al hacer clic en Cancelar', async () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Seleccionar el método Bizum
    await act(async () => {
      fireEvent.click(screen.getByText('Bizum'));
    });
    
    // Verificar que se muestra el diálogo
    expect(screen.getByText('Método de pago: Bizum')).toBeInTheDocument();
    
    // Cancelar
    await act(async () => {
      fireEvent.click(screen.getByText('Cancelar'));
    });
    
    // Verificar que el diálogo se cerró (ya no se muestra el título)
    expect(screen.queryByText('Método de pago: Bizum')).not.toBeInTheDocument();
  });

  it('llama a onBack al hacer clic en el botón Volver', async () => {
    render(<PaymentSelector onNext={mockOnNext} onBack={mockOnBack} amount={mockAmount} />);
    
    // Hacer clic en el botón Volver
    await act(async () => {
      fireEvent.click(screen.getByText('Volver'));
    });
    
    // Verificar que se llamó a onBack
    expect(mockOnBack).toHaveBeenCalled();
  });
});
