import { AddOutlined, CloudDownloadOutlined, DeleteOutline, EditOutlined } from '@mui/icons-material';
import { Box, Button, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, MenuItem, Stack, Tab, Tabs, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import EmptyState from '../components/EmptyState';
import LoadingScreen from '../components/LoadingScreen';
import PageHeader from '../components/PageHeader';
import { useNotification } from '../contexts/NotificationContext';
import { indiceService, type IndiceEconomicoPayload } from '../services/indiceService';
import type { IndiceEconomico, TipoIndice } from '../services/api';

const emptyForm: IndiceEconomicoPayload = {
  tipoIndice: 'SELIC',
  competencia: '2024-01-01',
  valorPercentual: 0,
  dataImportacao: new Date().toISOString().slice(0, 10),
  fonte: 'BACEN'
};

const tabs = [
  { value: 'SELIC', label: 'SELIC' },
  { value: 'IPCA', label: 'IPCA' },
  { value: 'IGPM', label: 'IGP-M' }
] as const;

export default function IndicesPage() {
  const { notify } = useNotification();
  const [indices, setIndices] = useState<IndiceEconomico[]>([]);
  const [activeTab, setActiveTab] = useState<'SELIC' | 'IPCA' | 'IGPM'>('SELIC');
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<IndiceEconomicoPayload>(emptyForm);

  const indicesFiltrados = useMemo(() => indices.filter((indice) => indice.tipoIndice === activeTab), [activeTab, indices]);

  const carregar = async () => {
    setLoading(true);
    try {
      setIndices(await indiceService.listar());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void carregar();
  }, []);

  const abrirNovo = () => {
    setEditingId(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const abrirEdicao = (indice: IndiceEconomico) => {
    setEditingId(indice.id);
    setForm({
      tipoIndice: indice.tipoIndice,
      competencia: `${indice.competencia}-01`,
      valorPercentual: indice.valorPercentual,
      dataImportacao: indice.dataImportacao,
      fonte: indice.fonte
    });
    setDialogOpen(true);
  };

  const salvar = async () => {
    try {
      if (editingId) {
        await indiceService.atualizar(editingId, form);
        notify({ severity: 'success', message: 'Índice atualizado com sucesso.' });
      } else {
        await indiceService.criar(form);
        notify({ severity: 'success', message: 'Índice criado com sucesso.' });
      }
      setDialogOpen(false);
      await carregar();
    } catch {
      notify({ severity: 'error', message: 'Não foi possível salvar o índice.' });
    }
  };

  const excluir = async (id: number) => {
    await indiceService.excluir(id);
    notify({ severity: 'success', message: 'Índice excluído.' });
    await carregar();
  };

  const importarSelic = async () => {
    setLoading(true);
    try {
      const response = await indiceService.importarSelic();
      notify({
        severity: response.persistido ? 'success' : 'warning',
        message: `${response.tipoIndice} importado com ${response.registrosImportados} registros. Persistido no banco: ${response.registrosPersistidos}.`
      });
      await carregar();
    } catch {
      notify({ severity: 'error', message: 'Não foi possível importar a tabela SELIC.' });
      setLoading(false);
    }
  };

  const importarIpca = async () => {
    setLoading(true);
    try {
      const response = await indiceService.importarIpca();
      notify({
        severity: response.persistido ? 'success' : 'warning',
        message: `${response.tipoIndice} importado com ${response.registrosImportados} registros. Persistido no banco: ${response.registrosPersistidos}.`
      });
      await carregar();
    } catch {
      notify({ severity: 'error', message: 'Não foi possível importar a tabela IPCA.' });
      setLoading(false);
    }
  };

  const importarIgpm = async () => {
    setLoading(true);
    try {
      const response = await indiceService.importarIgpm();
      notify({
        severity: response.persistido ? 'success' : 'warning',
        message: `${response.tipoIndice} importado com ${response.registrosImportados} registros. Persistido no banco: ${response.registrosPersistidos}.`
      });
      await carregar();
    } catch {
      notify({ severity: 'error', message: 'Não foi possível importar a tabela IGP-M.' });
      setLoading(false);
    }
  };

  const renderImportButton = () => {
    if (activeTab === 'SELIC') {
      return (
        <Button startIcon={<CloudDownloadOutlined />} variant="outlined" onClick={() => void importarSelic()}>
          Importar SELIC
        </Button>
      );
    }

    if (activeTab === 'IPCA') {
      return (
        <Button startIcon={<CloudDownloadOutlined />} variant="outlined" onClick={() => void importarIpca()}>
          Importar IPCA
        </Button>
      );
    }

    if (activeTab === 'IGPM') {
      return (
        <Button startIcon={<CloudDownloadOutlined />} variant="outlined" onClick={() => void importarIgpm()}>
          Importar IGP-M
        </Button>
      );
    }

    return null;
  };

  return (
    <Stack spacing={3}>
      <PageHeader title="Gerenciar Índices Econômicos" subtitle="CRUD completo para SELIC, IPCA e IGP-M, com base para importação via API ou CSV no futuro." showBackButton backTo="/" />
      <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider' }}>
        <CardContent>
          <Stack spacing={2} mb={2}>
            <Stack direction="row" justifyContent="space-between" alignItems="center" flexWrap="wrap" gap={2}>
              <Typography variant="h6" fontWeight={800}>Tabela de índices</Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
                {renderImportButton()}
                <Button startIcon={<AddOutlined />} variant="contained" onClick={abrirNovo}>Novo índice</Button>
              </Stack>
            </Stack>
            <Tabs
              value={activeTab}
              onChange={(_, nextTab) => setActiveTab(nextTab)}
              variant="scrollable"
              scrollButtons="auto"
              allowScrollButtonsMobile
            >
              {tabs.map((tab) => (
                <Tab key={tab.value} value={tab.value} label={tab.label} />
              ))}
            </Tabs>
          </Stack>
          {loading ? (
            <LoadingScreen label="Carregando índices..." />
          ) : indicesFiltrados.length === 0 ? (
            <EmptyState title="Nenhum índice cadastrado" message="Cadastre SELIC, IPCA e IGP-M para habilitar os cálculos judiciais." />
          ) : (
            <Box sx={{ overflowX: 'auto' }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Tipo</TableCell>
                    <TableCell>Competência</TableCell>
                    <TableCell>Valor</TableCell>
                    <TableCell>Fonte</TableCell>
                    <TableCell align="right">Ações</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {indicesFiltrados.map((indice) => (
                    <TableRow key={indice.id} hover>
                      <TableCell>{indice.tipoIndice}</TableCell>
                      <TableCell>{indice.competencia}</TableCell>
                      <TableCell>{indice.valorPercentual.toFixed(2)}%</TableCell>
                      <TableCell>{indice.fonte}</TableCell>
                      <TableCell align="right">
                        <IconButton onClick={() => abrirEdicao(indice)}><EditOutlined fontSize="small" /></IconButton>
                        <IconButton onClick={() => void excluir(indice.id)}><DeleteOutline fontSize="small" /></IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          )}
        </CardContent>
      </Card>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{editingId ? 'Editar índice' : 'Novo índice'}</DialogTitle>
        <DialogContent sx={{ display: 'grid', gap: 2, pt: 2 }}>
          <TextField select label="Tipo índice" value={form.tipoIndice} onChange={(event) => setForm({ ...form, tipoIndice: event.target.value as TipoIndice })} fullWidth>
            {(['SELIC', 'IPCA', 'IGPM'] as TipoIndice[]).map((tipo) => <MenuItem key={tipo} value={tipo}>{tipo}</MenuItem>)}
          </TextField>
          <TextField label="Competência" type="date" value={form.competencia} onChange={(event) => setForm({ ...form, competencia: event.target.value })} fullWidth InputLabelProps={{ shrink: true }} />
          <TextField label="Valor percentual" type="number" value={form.valorPercentual} onChange={(event) => setForm({ ...form, valorPercentual: Number(event.target.value) })} fullWidth inputProps={{ step: '0.01' }} />
          <TextField label="Data importação" type="date" value={form.dataImportacao} onChange={(event) => setForm({ ...form, dataImportacao: event.target.value })} fullWidth InputLabelProps={{ shrink: true }} />
          <TextField label="Fonte" value={form.fonte} onChange={(event) => setForm({ ...form, fonte: event.target.value })} fullWidth />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancelar</Button>
          <Button variant="contained" onClick={() => void salvar()}>Salvar</Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}