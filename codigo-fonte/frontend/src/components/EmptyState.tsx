import { Box, Typography } from '@mui/material';

export default function EmptyState({ title, message }: { title: string; message: string }) {
  return (
    <Box sx={{ textAlign: 'center', py: 6 }}>
      <Typography variant="h6" fontWeight={700} gutterBottom>
        {title}
      </Typography>
      <Typography color="text.secondary">{message}</Typography>
    </Box>
  );
}