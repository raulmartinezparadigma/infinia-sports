// Configuración para silenciar advertencias específicas en los tests
// Silenciar advertencias de MUI Grid props obsoletas
const originalConsoleWarn = console.warn;
console.warn = (...args) => {
  // Ignorar advertencias específicas de MUI Grid
  if (args[0]?.includes?.('MUI Grid: The `item` prop has been removed')) return;
  if (args[0]?.includes?.('MUI Grid: The `xs` prop has been removed')) return;
  if (args[0]?.includes?.('MUI Grid: The `sm` prop has been removed')) return;
  originalConsoleWarn(...args);
};
