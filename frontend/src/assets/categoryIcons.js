import CheckroomIcon from '@mui/icons-material/Checkroom';
import LocalDrinkIcon from '@mui/icons-material/LocalDrink';

const SneakerSVG = (
  <svg width="38" height="38" viewBox="0 0 38 38" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M6 28C6 25 8 23 11 23H28C31 23 32 25.5 32 27C32 28.5 30.5 30 28 30H10C7.5 30 6 29 6 28Z" fill="#1976d2"/>
    <path d="M11 23C11 18 16 13 22 13C27 13 32 18 32 23" stroke="#1976d2" strokeWidth="2" strokeLinecap="round"/>
    <circle cx="13.5" cy="27" r="1.2" fill="#fff" />
    <circle cx="18.5" cy="27" r="1.2" fill="#fff" />
    <circle cx="23.5" cy="27" r="1.2" fill="#fff" />
  </svg>
);

export const CATEGORY_ICONS = {
  SNEAKERS: {
    label: 'Zapatillas',
    icon: SneakerSVG
  },
  CLOTHING: {
    label: 'Ropa',
    icon: <CheckroomIcon style={{ fontSize: 38, color: '#43a047' }} />
  },
  SUPPLEMENT: {
    label: 'Suplemento',
    icon: <LocalDrinkIcon style={{ fontSize: 38, color: '#ff9800' }} />
  }
};
