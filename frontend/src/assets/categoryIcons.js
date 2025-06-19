// Iconos SVG para categorías. Puedes reemplazar los SVG por imágenes si lo prefieres.
export const CATEGORY_ICONS = {
  SNEAKERS: {
    label: 'Zapatillas',
    icon: (
      <svg width="38" height="38" viewBox="0 0 38 38" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="38" height="38" rx="12" fill="#F5F5F5"/>
        <path d="M7 28L31 28L27 18L11 18L7 28Z" fill="#1976d2"/>
        <rect x="13" y="14" width="12" height="4" rx="2" fill="#1976d2"/>
      </svg>
    )
  },
  CLOTHING: {
    label: 'Ropa',
    icon: (
      <svg width="38" height="38" viewBox="0 0 38 38" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="38" height="38" rx="12" fill="#F5F5F5"/>
        <path d="M19 10L26 14V28H12V14L19 10Z" fill="#43a047"/>
      </svg>
    )
  },
  SUPPLEMENT: {
    label: 'Suplemento',
    icon: (
      <svg width="38" height="38" viewBox="0 0 38 38" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="38" height="38" rx="12" fill="#F5F5F5"/>
        <circle cx="19" cy="19" r="7" fill="#ff9800"/>
        <rect x="15" y="10" width="8" height="4" rx="2" fill="#ff9800"/>
      </svg>
    )
  }
};
