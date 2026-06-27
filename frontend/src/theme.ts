import { alpha, createTheme } from '@mui/material/styles';

export type AppThemeMode = 'light' | 'dark';

export const createAppTheme = (mode: AppThemeMode) => createTheme({
  palette: {
    mode,
    primary: {
      main: mode === 'dark' ? '#8db4ff' : '#123a5a'
    },
    secondary: {
      main: mode === 'dark' ? '#f0b36d' : '#c0892c'
    },
    background: {
      default: mode === 'dark' ? '#0b1220' : '#f4f7fb',
      paper: mode === 'dark' ? '#111b2d' : '#ffffff'
    },
    text: {
      primary: mode === 'dark' ? '#e8eef8' : '#152333',
      secondary: mode === 'dark' ? '#b9c5d6' : '#5c6b7a'
    },
    divider: mode === 'dark' ? 'rgba(141,180,255,0.14)' : 'rgba(18,58,90,0.08)'
  },
  shape: {
    borderRadius: 16
  },
  typography: {
    fontFamily: ['Inter', 'Segoe UI', 'Arial', 'sans-serif'].join(',')
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundColor: mode === 'dark' ? '#0b1220' : '#f4f7fb'
        },
        'input[type="date"]::-webkit-calendar-picker-indicator': {
          cursor: 'pointer',
          opacity: mode === 'dark' ? 0.92 : 0.7,
          filter: mode === 'dark'
            ? 'brightness(0) saturate(100%) invert(75%) sepia(37%) saturate(631%) hue-rotate(183deg) brightness(102%) contrast(101%)'
            : 'none'
        }
      }
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backdropFilter: 'blur(14px)',
          borderBottom: `1px solid ${mode === 'dark' ? 'rgba(141,180,255,0.16)' : 'rgba(18,58,90,0.08)'}`
        }
      }
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          boxShadow: mode === 'dark' ? `0 18px 48px ${alpha('#000', 0.25)}` : '0 12px 28px rgba(18,58,90,0.06)'
        }
      }
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none'
        }
      }
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 700,
          borderRadius: 999
        }
      }
    }
  }
});