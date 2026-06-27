export type TipoIndice = 'SELIC' | 'IPCA' | 'IGPM';
export type TipoCalculo = 'ATUALIZACAO_MONETARIA' | 'CALCULO_REVERSO';

export type IndiceEconomico = {
  id: number;
  tipoIndice: TipoIndice;
  competencia: string;
  valorPercentual: number;
  dataImportacao: string;
  fonte: string;
};

export type CalculoMemoria = {
  competencia: string;
  indicePercentual: number;
  valorAnterior: number;
  valorCorrigido: number;
};

export type CalculoPeriodo = {
  ordem: number;
  dataInicial: string;
  dataFinal: string;
  indiceUtilizado: TipoIndice;
};

export type CalculoResumo = {
  id: number;
  nomeCalculo?: string | null;
  tipoCalculo: TipoCalculo;
  indiceUtilizado: TipoIndice;
  valorOriginal: number;
  valorFinal: number;
  dataInicial: string;
  dataFinal: string;
  dataCriacao: string;
};

export type CalculoDetalhe = CalculoResumo & {
  periodos: CalculoPeriodo[];
  memoria: CalculoMemoria[];
};