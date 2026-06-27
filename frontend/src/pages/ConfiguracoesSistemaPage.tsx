import { DarkModeOutlined, LightModeOutlined } from '@mui/icons-material';
import { Button, Card, CardContent, Stack, ToggleButton, ToggleButtonGroup, Typography } from '@mui/material';
import PageHeader from '../components/PageHeader';
import { useThemeMode } from '../contexts/ThemeModeContext';

export default function ConfiguracoesSistemaPage() {
  const { mode, setMode } = useThemeMode();

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Configurações do sistema"
        subtitle="Defina a aparência geral da aplicação e mantenha sua preferência salva no navegador."
        showBackButton
        backTo="/"
      />

      <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider' }}>
        <CardContent>
          <Stack spacing={3} maxWidth={720}>
            <Stack spacing={1}>
              <Typography variant="h6" fontWeight={800}>Tema visual</Typography>
              <Typography color="text.secondary">
                Escolha entre modo claro e modo escuro. A preferência fica salva automaticamente.
              </Typography>
            </Stack>

            <ToggleButtonGroup
              value={mode}
              exclusive
              onChange={(_, nextMode) => {
                if (nextMode) {
                  setMode(nextMode);
                }
              }}
              sx={{ alignSelf: 'flex-start' }}
            >
              <ToggleButton value="light">
                <LightModeOutlined sx={{ mr: 1 }} />
                Modo claro
              </ToggleButton>
              <ToggleButton value="dark">
                <DarkModeOutlined sx={{ mr: 1 }} />
                Modo escuro
              </ToggleButton>
            </ToggleButtonGroup>

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <Card elevation={0} sx={{ flex: 1, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
                <CardContent>
                  <Typography variant="overline" color="text.secondary">Preferência atual</Typography>
                  <Typography variant="h5" fontWeight={900} color="primary.main">
                    {mode === 'dark' ? 'Modo escuro' : 'Modo claro'}
                  </Typography>
                </CardContent>
              </Card>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}