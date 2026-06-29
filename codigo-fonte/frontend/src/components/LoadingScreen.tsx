import { Box, CircularProgress, Typography } from '@mui/material';

export default function LoadingScreen({ label = 'Processando...' }: { label?: string }) {
  return (
    <Box sx={{ display: 'grid', placeItems: 'center', minHeight: 280, gap: 2 }}>
      <CircularProgress />
      <Typography color="text.secondary">{label}</Typography>
    </Box>
  );
}