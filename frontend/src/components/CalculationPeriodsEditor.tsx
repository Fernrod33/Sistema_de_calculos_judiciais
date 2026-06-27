import { AddOutlined, DeleteOutline } from '@mui/icons-material';
import { Box, Button, Card, CardContent, Grid2 as Grid, IconButton, MenuItem, Stack, TextField, Typography } from '@mui/material';
import type { TipoIndice } from '../services/api';
import type { CalculoPeriodoPayload } from '../services/calculoService';

const indices: TipoIndice[] = ['SELIC', 'IPCA', 'IGPM'];

export function createPeriodoCalculo(payload?: Partial<CalculoPeriodoPayload>): CalculoPeriodoPayload {
  return {
    dataInicial: '',
    dataFinal: '',
    indiceUtilizado: 'SELIC',
    ...payload
  };
}

type Props = {
  value: CalculoPeriodoPayload[];
  onChange: (periodos: CalculoPeriodoPayload[]) => void;
};

export default function CalculationPeriodsEditor({ value, onChange }: Props) {
  const atualizarPeriodo = (index: number, field: keyof CalculoPeriodoPayload, fieldValue: string) => {
    onChange(value.map((periodo, periodoIndex) => (
      periodoIndex === index ? { ...periodo, [field]: fieldValue } as CalculoPeriodoPayload : periodo
    )));
  };

  const adicionarPeriodo = () => {
    const anterior = value[value.length - 1];
    onChange([
      ...value,
      createPeriodoCalculo(anterior?.dataFinal ? { dataInicial: anterior.dataFinal } : undefined)
    ]);
  };

  const removerPeriodo = (index: number) => {
    if (value.length === 1) {
      return;
    }
    onChange(value.filter((_, periodoIndex) => periodoIndex !== index));
  };

  return (
    <Box sx={{ display: 'grid', gap: 2 }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
        <Box>
          <Typography variant="h6" fontWeight={800}>Períodos do cálculo</Typography>
          <Typography variant="body2" color="text.secondary">Adicione blocos com índices diferentes para compor uma única conta.</Typography>
        </Box>
        <Button variant="outlined" startIcon={<AddOutlined />} onClick={adicionarPeriodo}>Adicionar período</Button>
      </Stack>

      {value.map((periodo, index) => (
        <Card key={`${index}-${periodo.dataInicial}-${periodo.dataFinal}`} variant="outlined">
          <CardContent>
            <Stack spacing={2}>
              <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
                <Typography fontWeight={700}>Período {index + 1}</Typography>
                <IconButton onClick={() => removerPeriodo(index)} disabled={value.length === 1} aria-label={`Remover período ${index + 1}`}>
                  <DeleteOutline fontSize="small" />
                </IconButton>
              </Stack>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <TextField
                    label="Data inicial"
                    type="date"
                    value={periodo.dataInicial}
                    onChange={(event) => atualizarPeriodo(index, 'dataInicial', event.target.value)}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <TextField
                    label="Data final"
                    type="date"
                    value={periodo.dataFinal}
                    onChange={(event) => atualizarPeriodo(index, 'dataFinal', event.target.value)}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <TextField
                    select
                    label="Índice"
                    value={periodo.indiceUtilizado}
                    onChange={(event) => atualizarPeriodo(index, 'indiceUtilizado', event.target.value)}
                    fullWidth
                  >
                    {indices.map((indice) => (
                      <MenuItem key={indice} value={indice}>{indice}</MenuItem>
                    ))}
                  </TextField>
                </Grid>
              </Grid>
            </Stack>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
}