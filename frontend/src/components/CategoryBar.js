import React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import { CATEGORY_ICONS } from '../assets/categoryIcons';

function CategoryBar({ selected, onSelect }) {
  return (
    <Box sx={{
      display: 'flex',
      overflowX: 'auto',
      gap: 2,
      py: 2,
      px: 1,
      background: '#fff',
      borderRadius: 2,
      boxShadow: '0 2px 8px #b3c6ff22',
      mb: 2,
      alignItems: 'center',
      minHeight: 80
    }}>
      {Object.entries(CATEGORY_ICONS).map(([key, { label, icon }]) => (
        <Button
          key={key}
          onClick={() => onSelect(key)}
          variant={selected === key ? 'contained' : 'text'}
          color={selected === key ? 'primary' : 'inherit'}
          sx={{
            display: 'flex', flexDirection: 'column', alignItems: 'center', minWidth: 90, px: 1, py: 1,
            borderRadius: 2, fontWeight: 600, fontSize: 13
          }}
        >
          <span style={{ marginBottom: 4 }}>{icon}</span>
          {label}
        </Button>
      ))}
      <Button
        onClick={() => onSelect('')}
        variant={selected === '' ? 'contained' : 'text'}
        color={selected === '' ? 'primary' : 'inherit'}
        sx={{ minWidth: 90, px: 1, py: 1, borderRadius: 2, fontWeight: 600, fontSize: 13 }}
      >
        Todos
      </Button>
    </Box>
  );
}

export default CategoryBar;
