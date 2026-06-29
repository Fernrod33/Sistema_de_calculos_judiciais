import { api } from '../hooks/useApi';
import type { CalculoDetalhe, CalculoResumo, TipoIndice } from './api';

export type CalculoPeriodoPayload = {
  dataInicial: string;
  dataFinal: string;
  indiceUtilizado: TipoIndice;
};

export type AtualizacaoPayload = {
  valorInicial: number;
  nomeCalculo?: string;
  periodos: CalculoPeriodoPayload[];
};

export type ReversoPayload = {
  valorAtual: number;
  nomeCalculo?: string;
  periodos: CalculoPeriodoPayload[];
};

export const calculoService = {
  atualizar: async (payload: AtualizacaoPayload) => {
    const response = await api.post<CalculoDetalhe>('/calculos/atualizacao', payload);
    return response.data;
  },
  reverso: async (payload: ReversoPayload) => {
    const response = await api.post<CalculoDetalhe>('/calculos/reverso', payload);
    return response.data;
  },
  listar: async (): Promise<CalculoResumo[]> => {
    const response = await api.get<CalculoResumo[]>('/calculos');
    return response.data;
  },
  detalhe: async (id: number): Promise<CalculoDetalhe> => {
    const response = await api.get<CalculoDetalhe>(`/calculos/${id}`);
    return response.data;
  },
  excluir: async (id: number): Promise<void> => {
    await api.delete(`/calculos/${id}`);
  }
};