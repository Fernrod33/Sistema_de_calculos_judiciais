import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import App from './App';
import { createAppTheme } from './theme';
import { NotificationProvider } from './contexts/NotificationContext';
import { ThemeModeProvider, useThemeMode } from './contexts/ThemeModeContext';

function AppThemeProvider() {
  const { mode } = useThemeMode();

  return (
    <ThemeProvider theme={createAppTheme(mode)}>
      <CssBaseline />
      <BrowserRouter>
        <NotificationProvider>
          <App />
        </NotificationProvider>
      </BrowserRouter>
    </ThemeProvider>
  );
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <ThemeModeProvider>
      <AppThemeProvider />
    </ThemeModeProvider>
  </React.StrictMode>
);