import { Box, Button, Card, CardContent, Grid2 as Grid, Stack, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { isAxiosError } from 'axios';
import CalculationMemoryTable from '../components/CalculationMemoryTable';
import CalculationPeriodsEditor, { createPeriodoCalculo } from '../components/CalculationPeriodsEditor';
import CalculationPeriodsSummary from '../components/CalculationPeriodsSummary';
import CurrencyField from '../components/CurrencyField';
import LoadingScreen from '../components/LoadingScreen';
import PageHeader from '../components/PageHeader';
import { useNotification } from '../contexts/NotificationContext';
import { calculoService } from '../services/calculoService';
import { relatorioService } from '../services/relatorioService';
import type { CalculoDetalhe } from '../services/api';
import { formatMoney, parseMoney } from '../utils/money';
import { usePersistentState } from '../hooks/usePersistentState';

function extractErrorMessage(error: unknown, fallback: string) {
  if (isAxiosError(error)) {
    return (error.response?.data as { message?: string } | undefined)?.message ?? error.message ?? fallback;
  }

  if (error instanceof Error && error.message) {
    return error.message;
  }

  return fallback;
}

export default function AtualizacaoMonetariaPage() {
  const { notify } = useNotification();
  const navigate = useNavigate();
  const [valorInicial, setValorInicial] = usePersistentState('scj-atualizacao-valorInicial', '1000,00');
  const [nomeCalculo, setNomeCalculo] = usePersistentState('scj-atualizacao-nomeCalculo', '');
  const [periodos, setPeriodos] = usePersistentState('scj-atualizacao-periodos-v2', [createPeriodoCalculo({ dataInicial: '2024-01-01', dataFinal: '2024-03-31', indiceUtilizado: 'SELIC' })]);
  const [resultado, setResultado] = usePersistentState<CalculoDetalhe | null>('scj-atualizacao-resultado', null);
  const [loading, setLoading] = useState(false);
  const periodosAplicados = resultado?.periodos ?? [];

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await calculoService.atualizar({
        valorInicial: parseMoney(valorInicial),
        nomeCalculo: nomeCalculo.trim() || undefined,
        periodos
      });
      setResultado(response);
      notify({ severity: 'success', message: 'Cálculo de atualização monetária realizado com sucesso.' });
    } catch (error) {
      notify({ severity: 'error', message: extractErrorMessage(error, 'Não foi possível realizar o cálculo de atualização.') });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader title="Novo Cálculo de Atualização Monetária" subtitle="Atualize valores mês a mês com memória detalhada." showBackButton backTo="/" />
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, lg: 5 }}>
          <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider' }}>
            <CardContent>
              <Box component="form" onSubmit={handleSubmit} sx={{ display: 'grid', gap: 2 }}>
                <CurrencyField label="Valor inicial" value={valorInicial} onChange={setValorInicial} fullWidth />
                <TextField
                  label="Nome do cálculo"
                  value={nomeCalculo}
                  onChange={(event) => setNomeCalculo(event.target.value)}
                  fullWidth
                  helperText="Esse nome vai aparecer no relatório PDF no lugar do número da conta."
                />
                <CalculationPeriodsEditor value={periodos} onChange={setPeriodos} />
                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
                  <Button type="submit" variant="contained" size="large" disabled={loading}>
                    Calcular atualização
                  </Button>
                  <Button
                    variant="outlined"
                    size="large"
                    disabled={!resultado}
                    onClick={async () => {
                      if (!resultado) {
                        return;
                      }

                      try {
                        const blob = await relatorioService.baixarPdf(Number(resultado.id));
                        const url = URL.createObjectURL(blob);
                        const anchor = document.createElement('a');
                        anchor.href = url;
                        anchor.download = `relatorio-calculo-${resultado.id}.pdf`;
                        document.body.appendChild(anchor);
                        anchor.click();
                        window.setTimeout(() => {
                          URL.revokeObjectURL(url);
                          anchor.remove();
                        }, 1000);
                        notify({ severity: 'success', message: 'Relatório PDF gerado com sucesso.' });
                      } catch (error) {
                        notify({ severity: 'error', message: extractErrorMessage(error, 'Não foi possível gerar o relatório PDF.') });
                      }
                    }}
                  >
                    Gerar PDF
                  </Button>
                </Stack>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, lg: 7 }}>
          <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider', minHeight: 420 }}>
            <CardContent>
              {loading ? (
                <LoadingScreen label="Calculando atualização monetária..." />
              ) : resultado ? (
                <Stack spacing={3}>
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <Card variant="outlined" sx={{ flex: 1 }}>
                      <CardContent>
                        <Typography color="text.secondary" variant="body2">Valor final</Typography>
                        <Typography variant="h4" fontWeight={800} color="primary.main">{formatMoney(resultado.valorFinal)}</Typography>
                      </CardContent>
                    </Card>
                    <Card variant="outlined" sx={{ flex: 1 }}>
                      <CardContent>
                        <Typography color="text.secondary" variant="body2">ID do cálculo</Typography>
                        <Typography variant="h4" fontWeight={800}>{resultado.id}</Typography>
                      </CardContent>
                    </Card>
                    <Card variant="outlined" sx={{ flex: 1 }}>
                      <CardContent>
                        <Typography color="text.secondary" variant="body2">Períodos aplicados</Typography>
                        <Typography variant="h4" fontWeight={800}>{periodosAplicados.length}</Typography>
                      </CardContent>
                    </Card>
                  </Stack>
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <Button variant="outlined" onClick={async () => {
                      try {
                        const blob = await relatorioService.baixarPdf(Number(resultado.id));
                        const url = URL.createObjectURL(blob);
                        const anchor = document.createElement('a');
                        anchor.href = url;
                        anchor.download = `relatorio-calculo-${resultado.id}.pdf`;
                        document.body.appendChild(anchor);
                        anchor.click();
                        window.setTimeout(() => {
                          URL.revokeObjectURL(url);
                          anchor.remove();
                        }, 1000);
                        notify({ severity: 'success', message: 'Relatório PDF gerado com sucesso.' });
                      } catch (error) {
                        notify({ severity: 'error', message: extractErrorMessage(error, 'Não foi possível gerar o relatório PDF.') });
                      }
                    }}>
                      Gerar relatório deste cálculo
                    </Button>
                    <Button variant="text" onClick={() => navigate('/historico')}>
                      Ver no histórico
                    </Button>
                  </Stack>
                  <CalculationPeriodsSummary periodos={periodosAplicados} />
                  <CalculationMemoryTable memoria={resultado.memoria} />
                </Stack>
              ) : (
                <Typography color="text.secondary">Preencha o formulário para visualizar a memória de cálculo e o resultado atualizado.</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
}