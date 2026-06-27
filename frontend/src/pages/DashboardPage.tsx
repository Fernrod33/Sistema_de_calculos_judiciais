import { AccountBalanceOutlined, BalanceOutlined, HistoryOutlined, PictureAsPdfOutlined, SettingsOutlined, SwapHorizOutlined, TrendingUpOutlined } from '@mui/icons-material';
import { Card, CardActionArea, CardContent, Grid2 as Grid, Stack, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import PageHeader from '../components/PageHeader';

const actions = [
  { title: 'Novo Cálculo de Atualização Monetária', icon: <TrendingUpOutlined />, path: '/atualizacao' },
  { title: 'Novo Cálculo Reverso', icon: <SwapHorizOutlined />, path: '/reverso' },
  { title: 'Gerenciar Índices Econômicos', icon: <AccountBalanceOutlined />, path: '/indices' },
  { title: 'Histórico de Cálculos', icon: <HistoryOutlined />, path: '/historico' },
  { title: 'Consolidar Contas', icon: <BalanceOutlined />, path: '/consolidacao' },
  { title: 'Configurações do sistema', icon: <SettingsOutlined />, path: '/configuracoes' }
];

export default function DashboardPage() {
  const navigate = useNavigate();

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Dashboard"
        subtitle="Central operacional para cálculos judiciais, gestão de índices oficiais e emissão de relatórios jurídicos."
      />
      <Grid container spacing={3}>
        {actions.map((action) => (
          <Grid key={action.title} size={{ xs: 12, sm: 6, md: 4 }}>
            <Card elevation={0} sx={{ height: '100%', border: '1px solid', borderColor: 'divider' }}>
              <CardActionArea sx={{ height: '100%' }} onClick={() => navigate(action.path)}>
                <CardContent sx={{ p: 3 }}>
                  <Stack spacing={2}>
                    <Stack direction="row" alignItems="center" spacing={1.5}>
                      <Stack sx={{ width: 46, height: 46, borderRadius: 3, bgcolor: 'action.hover', color: 'primary.main', alignItems: 'center', justifyContent: 'center' }}>
                        {action.icon}
                      </Stack>
                      <Typography variant="h6" fontWeight={800} lineHeight={1.2}>
                        {action.title}
                      </Typography>
                    </Stack>
                    <Typography color="text.secondary">
                      Acesso rápido para operação jurídica com foco em atualização monetária, reversão e auditoria dos índices.
                    </Typography>
                  </Stack>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Stack>
  );
}