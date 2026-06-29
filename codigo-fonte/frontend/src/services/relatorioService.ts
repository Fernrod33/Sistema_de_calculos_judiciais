import { api } from '../hooks/useApi';

export const relatorioService = {
  baixarPdf: async (id: number): Promise<Blob> => {
    const response = await api.get(`/relatorios/${id}/pdf`, { responseType: 'blob' });
    return response.data;
  },
  baixarPdfConsolidado: async (ids: number[]): Promise<Blob> => {
    const response = await api.post('/relatorios/consolidado/pdf', ids, { responseType: 'blob' });
    return response.data;
  }
};