import { ArrowBackOutlined } from '@mui/icons-material';
import { Box, Button, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

type PageHeaderProps = {
  title: string;
  subtitle?: string;
  showBackButton?: boolean;
  backTo?: string;
};

export default function PageHeader({ title, subtitle, showBackButton = false, backTo }: PageHeaderProps) {
  const navigate = useNavigate();

  return (
    <Box sx={{ mb: 3 }}>
      {showBackButton ? (
        <Button
          startIcon={<ArrowBackOutlined />}
          onClick={() => {
            if (backTo) {
              navigate(backTo);
              return;
            }
            navigate(-1);
          }}
          sx={{ mb: 1.5 }}
        >
          Voltar
        </Button>
      ) : null}
      <Typography variant="h4" fontWeight={800} color="primary.main" gutterBottom>
        {title}
      </Typography>
      {subtitle ? (
        <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 820 }}>
          {subtitle}
        </Typography>
      ) : null}
    </Box>
  );
}