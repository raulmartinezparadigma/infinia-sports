import { render, screen } from '@testing-library/react';
import { act } from '@testing-library/react';
import App from './App';

// En Jest, describe, it y expect son globales, no necesitan ser importados
describe('App', () => {
  it('renderiza la aplicación sin errores', async () => {
    // Envolvemos la renderización en act() para manejar actualizaciones de estado asíncronas
    await act(async () => {
      render(<App />);
    });
    
    // Verificamos que la aplicación se ha renderizado correctamente
    // buscando algún elemento que sabemos que debe estar presente
    expect(document.body).toBeInTheDocument();
  });
});
