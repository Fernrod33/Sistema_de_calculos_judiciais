import { CalculateOutlined, PictureAsPdfOutlined } from '@mui/icons-material';
import { Box, Button, Card, CardContent, Checkbox, Divider, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import PageHeader from '../components/PageHeader';
import LoadingScreen from '../components/LoadingScreen';
import EmptyState from '../components/EmptyState';
import { useNotification } from '../contexts/NotificationContext';
import { calculoService } from '../services/calculoService';
import { relatorioService } from '../services/relatorioService';
import type { CalculoResumo } from '../services/api';
import { formatMoney } from '../utils/money';
import { formatDateTime } from '../utils/date';
import { usePersistentState } from '../hooks/usePersistentState';

export default function ConsolidacaoPage() {
  const { notify } = useNotification();
  const [calculos, setCalculos] = useState<CalculoResumo[]>([]);
  const [loading, setLoading] = useState(true);
  const [selecionados, setSelecionados] = usePersistentState<number[]>('scj-consolidacao-selecionados', []);

  useEffect(() => {
    void (async () => {
      setLoading(true);
      try {
        setCalculos(await calculoService.listar());
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const calculosPorId = useMemo(() => new Map(calculos.map((calculo) => [calculo.id, calculo])), [calculos]);

  const totaisSelecionados = useMemo(() => selecionados
    .map((id) => calculosPorId.get(id))
    .filter((calculo): calculo is CalculoResumo => Boolean(calculo)), [calculosPorId, selecionados]);

  const somaTotais = useMemo(() => totaisSelecionados.reduce((total, calculo) => total + Number(calculo.valorFinal), 0), [totaisSelecionados]);

  const toggleSelecionado = (id: number) => {
    setSelecionados((atual) => atual.includes(id) ? atual.filter((item) => item !== id) : [...atual, id]);
  };

  const selecionarTodos = () => {
    setSelecionados(calculos.map((calculo) => calculo.id));
  };

  const limpar = () => setSelecionados([]);

  const baixarPdf = async () => {
    try {
      const blob = await relatorioService.baixarPdfConsolidado(selecionados);
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = 'relatorio-consolidado.pdf';
      document.body.appendChild(anchor);
      anchor.click();
      window.setTimeout(() => {
        URL.revokeObjectURL(url);
        anchor.remove();
      }, 1000);
      notify({ severity: 'success', message: 'Relatório consolidado gerado com sucesso.' });
    } catch {
      notify({ severity: 'error', message: 'Não foi possível gerar o relatório consolidado.' });
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader title="Consolidação de Contas" subtitle="Selecione vários cálculos e veja a soma dos totais consolidados em um único painel." showBackButton backTo="/" />
      <Stack direction={{ xs: 'column', lg: 'row' }} spacing={3} alignItems="flex-start">
        <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider', width: '100%' }}>
          <CardContent>
            <Stack spacing={2}>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} justifyContent="space-between" alignItems={{ xs: 'stretch', md: 'center' }}>
                <Typography variant="h6" fontWeight={800}>Cálculos disponíveis</Typography>
                <Stack direction="row" spacing={1} flexWrap="wrap">
                  <Button variant="outlined" onClick={selecionarTodos} disabled={calculos.length === 0}>Selecionar todos</Button>
                  <Button variant="text" onClick={limpar} disabled={selecionados.length === 0}>Limpar seleção</Button>
                </Stack>
              </Stack>
              {loading ? (
                <LoadingScreen label="Carregando cálculos..." />
              ) : calculos.length === 0 ? (
                <EmptyState title="Nenhum cálculo encontrado" message="Faça ao menos um cálculo para consolidar várias contas e somar os totais." />
              ) : (
                <Box sx={{ overflowX: 'auto' }}>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell />
                        <TableCell>ID</TableCell>
                        <TableCell>Tipo</TableCell>
                        <TableCell>Índice</TableCell>
                        <TableCell>Valor final</TableCell>
                        <TableCell>Data</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {calculos.map((calculo) => {
                        const checked = selecionados.includes(calculo.id);
                        return (
                          <TableRow key={calculo.id} hover>
                            <TableCell>
                              <Checkbox checked={checked} onChange={() => toggleSelecionado(calculo.id)} />
                            </TableCell>
                            <TableCell>{calculo.id}</TableCell>
                            <TableCell>{calculo.tipoCalculo}</TableCell>
                            <TableCell>{calculo.indiceUtilizado}</TableCell>
                            <TableCell>{formatMoney(calculo.valorFinal)}</TableCell>
                            <TableCell>{formatDateTime(calculo.dataCriacao)}</TableCell>
                          </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </Box>
              )}
            </Stack>
          </CardContent>
        </Card>

        <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider', minWidth: { xs: '100%', lg: 340 } }}>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="h6" fontWeight={800}>Resumo consolidado</Typography>
              <Typography color="text.secondary">Contas selecionadas: {selecionados.length}</Typography>
              <Typography variant="h4" fontWeight={900} color="primary.main">
                {formatMoney(somaTotais)}
              </Typography>
              <Divider />
              <Stack spacing={1}>
                {totaisSelecionados.slice(0, 8).map((calculo) => (
                  <Stack key={calculo.id} direction="row" justifyContent="space-between" spacing={2}>
                    <Typography variant="body2">#{calculo.id}</Typography>
                    <Typography variant="body2" fontWeight={700}>{formatMoney(calculo.valorFinal)}</Typography>
                  </Stack>
                ))}
              </Stack>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
                <Button variant="contained" startIcon={<CalculateOutlined />} disabled={selecionados.length === 0} onClick={() => notify({ severity: 'success', message: 'Resumo consolidado atualizado.' })}>
                  Atualizar consolidado
                </Button>
                <Button variant="outlined" startIcon={<PictureAsPdfOutlined />} disabled={selecionados.length === 0} onClick={() => void baixarPdf()}>
                  Baixar PDF
                </Button>
              </Stack>
            </Stack>
          </CardContent>
        </Card>
      </Stack>
    </Stack>
  );
}