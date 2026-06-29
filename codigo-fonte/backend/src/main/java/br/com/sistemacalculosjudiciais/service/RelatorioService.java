package br.com.sistemacalculosjudiciais.service;

import java.util.List;

// Interface de serviço que define as operações de geração de relatórios em PDF
public interface RelatorioService {

    byte[] gerarPdf(Long calculoId);

    byte[] gerarPdfConsolidado(List<Long> calculoIds);
}