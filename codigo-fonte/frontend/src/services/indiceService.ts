import { api } from '../hooks/useApi';
import type { IndiceEconomico, TipoIndice } from './api';

export type IndiceEconomicoPayload = {
  tipoIndice: TipoIndice;
  competencia: string;
  valorPercentual: number;
  dataImportacao: string;
  fonte: string;
};

export type ImportacaoSelicResponse = {
  tipoIndice: string;
  registrosImportados: number;
  registrosPersistidos: number;
  origem: string;
  persistido: boolean;
};

export const indiceService = {
  listar: async (): Promise<IndiceEconomico[]> => {
    const response = await api.get<IndiceEconomico[]>('/indices');
    return response.data;
  },
  criar: async (payload: IndiceEconomicoPayload): Promise<IndiceEconomico> => {
    const response = await api.post<IndiceEconomico>('/indices', payload);
    return response.data;
  },
  atualizar: async (id: number, payload: IndiceEconomicoPayload): Promise<IndiceEconomico> => {
    const response = await api.put<IndiceEconomico>(`/indices/${id}`, payload);
    return response.data;
  },
  excluir: async (id: number): Promise<void> => {
    await api.delete(`/indices/${id}`);
  },
  importarSelic: async (): Promise<ImportacaoSelicResponse> => {
    const response = await api.post<ImportacaoSelicResponse>('/indices/importar-selic');
    return response.data;
  },
  importarIpca: async (): Promise<ImportacaoSelicResponse> => {
    const response = await api.post<ImportacaoSelicResponse>('/indices/importar-ipca');
    return response.data;
  },
  importarIgpm: async (): Promise<ImportacaoSelicResponse> => {
    const response = await api.post<ImportacaoSelicResponse>('/indices/importar-igpm');
    return response.data;
  }
};