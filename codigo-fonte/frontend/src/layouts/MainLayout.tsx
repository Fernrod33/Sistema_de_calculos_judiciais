import { keyframes } from '@emotion/react';
import { DarkModeOutlined, LightModeOutlined } from '@mui/icons-material';
import { AppBar, Box, Container, IconButton, Toolbar, Tooltip, Typography, useTheme } from '@mui/material';
import { Outlet, useLocation } from 'react-router-dom';
import { useMemo } from 'react';
import { useThemeMode } from '../contexts/ThemeModeContext';

const enter = keyframes`
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

export default function MainLayout() {
  const year = useMemo(() => new Date().getFullYear(), []);
  const location = useLocation();
  const theme = useTheme();
  const { mode, toggleMode } = useThemeMode();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: mode === 'dark'
          ? 'radial-gradient(circle at top, rgba(141,180,255,0.12), transparent 35%), linear-gradient(180deg, #0b1220 0%, #10192a 100%)'
          : `linear-gradient(180deg, ${theme.palette.background.default} 0%, ${theme.palette.background.paper} 100%)`
      }}
    >
      <AppBar position="sticky" elevation={0} color="transparent" sx={{ backgroundColor: 'transparent' }}>
        <Toolbar sx={{ gap: 2 }}>
          <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'secondary.main', boxShadow: '0 0 0 6px rgba(192,137,44,0.12)' }} />
          <Typography variant="h6" fontWeight={800} color="primary.main">
            JurisCalc
          </Typography>
          <Box sx={{ flex: 1 }} />
          <Tooltip title={mode === 'dark' ? 'Ativar modo claro' : 'Ativar modo escuro'}>
            <IconButton color="primary" onClick={toggleMode} aria-label="alternar tema">
              {theme.palette.mode === 'dark' ? <LightModeOutlined /> : <DarkModeOutlined />}
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ py: 4 }}>
        <Box key={location.pathname} sx={{ animation: `${enter} 240ms ease both` }}>
          <Outlet />
        </Box>
      </Container>
      <Box component="footer" sx={{ py: 3, textAlign: 'center', color: 'text.secondary' }}>
        <Typography variant="body2">© {year} JurisCalc</Typography>
      </Box>
    </Box>
  );
}