import { DeleteOutline, PictureAsPdfOutlined } from '@mui/icons-material';
import { Box, Card, CardContent, IconButton, MenuItem, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import EmptyState from '../components/EmptyState';
import LoadingScreen from '../components/LoadingScreen';
import PageHeader from '../components/PageHeader';
import { useNotification } from '../contexts/NotificationContext';
import { calculoService } from '../services/calculoService';
import { relatorioService } from '../services/relatorioService';
import type { CalculoResumo, TipoCalculo, TipoIndice } from '../services/api';
import { formatMoney } from '../utils/money';
import { formatDateTime } from '../utils/date';

export default function HistoricoPage() {
  const { notify } = useNotification();
  const navigate = useNavigate();
  const [calculos, setCalculos] = useState<CalculoResumo[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroIndice, setFiltroIndice] = useState<TipoIndice | 'TODOS'>('TODOS');
  const [filtroTipo, setFiltroTipo] = useState<TipoCalculo | 'TODOS'>('TODOS');

  const carregar = async () => {
    setLoading(true);
    try {
      setCalculos(await calculoService.listar());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void carregar();
  }, []);

  const calculosFiltrados = useMemo(() => calculos.filter((calculo) => {
    const indiceOk = filtroIndice === 'TODOS' || calculo.indiceUtilizado === filtroIndice;
    const tipoOk = filtroTipo === 'TODOS' || calculo.tipoCalculo === filtroTipo;
    return indiceOk && tipoOk;
  }), [calculos, filtroIndice, filtroTipo]);

  const excluir = async (id: number) => {
    await calculoService.excluir(id);
    notify({ severity: 'success', message: 'Cálculo removido do histórico.' });
    await carregar();
  };

  return (
    <Stack spacing={3}>
      <PageHeader title="Histórico de Cálculos" subtitle="Acompanhe os cálculos realizados, filtre por índice e tipo, e acesse os relatórios PDF." showBackButton backTo="/" />
      <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider' }}>
        <CardContent>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} mb={2}>
            <TextField select label="Filtrar por índice" value={filtroIndice} onChange={(event) => setFiltroIndice(event.target.value as typeof filtroIndice)} fullWidth>
              <MenuItem value="TODOS">Todos</MenuItem>
              <MenuItem value="SELIC">SELIC</MenuItem>
              <MenuItem value="IPCA">IPCA</MenuItem>
              <MenuItem value="IGPM">IGPM</MenuItem>
            </TextField>
            <TextField select label="Filtrar por tipo" value={filtroTipo} onChange={(event) => setFiltroTipo(event.target.value as typeof filtroTipo)} fullWidth>
              <MenuItem value="TODOS">Todos</MenuItem>
              <MenuItem value="ATUALIZACAO_MONETARIA">Atualização monetária</MenuItem>
              <MenuItem value="CALCULO_REVERSO">Cálculo reverso</MenuItem>
            </TextField>
          </Stack>
          {loading ? (
            <LoadingScreen label="Carregando histórico..." />
          ) : calculosFiltrados.length === 0 ? (
            <EmptyState title="Nenhum cálculo encontrado" message="Os resultados de atualização e reversão aparecem aqui após o processamento." />
          ) : (
            <Box sx={{ overflowX: 'auto' }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Nome</TableCell>
                    <TableCell>Tipo</TableCell>
                    <TableCell>Índice</TableCell>
                    <TableCell>Valor original</TableCell>
                    <TableCell>Valor final</TableCell>
                    <TableCell>Data criação</TableCell>
                    <TableCell align="right">Ações</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {calculosFiltrados.map((calculo) => (
                    <TableRow key={calculo.id} hover>
                      <TableCell>
                        <Stack spacing={0.5}>
                          <Typography fontWeight={700}>{calculo.id}</Typography>
                          <Typography variant="caption" color="text.secondary">Identificação interna</Typography>
                        </Stack>
                      </TableCell>
                      <TableCell>{calculo.nomeCalculo ?? 'Sem nome'}</TableCell>
                      <TableCell>{calculo.tipoCalculo}</TableCell>
                      <TableCell>{calculo.indiceUtilizado}</TableCell>
                      <TableCell>{formatMoney(calculo.valorOriginal)}</TableCell>
                      <TableCell>{formatMoney(calculo.valorFinal)}</TableCell>
                      <TableCell>{formatDateTime(calculo.dataCriacao)}</TableCell>
                      <TableCell align="right">
                        <IconButton onClick={async () => {
                          try {
                            const blob = await relatorioService.baixarPdf(Number(calculo.id));
                            const url = URL.createObjectURL(blob);
                            const anchor = document.createElement('a');
                            anchor.href = url;
                            anchor.download = `relatorio-calculo-${calculo.id}.pdf`;
                            document.body.appendChild(anchor);
                            anchor.click();
                            window.setTimeout(() => {
                              URL.revokeObjectURL(url);
                              anchor.remove();
                            }, 1000);
                            notify({ severity: 'success', message: 'Relatório PDF gerado com sucesso.' });
                          } catch {
                            notify({ severity: 'error', message: 'Não foi possível gerar o relatório PDF.' });
                          }
                        }}><PictureAsPdfOutlined fontSize="small" /></IconButton>
                        <IconButton onClick={() => void excluir(calculo.id)}><DeleteOutline fontSize="small" /></IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}