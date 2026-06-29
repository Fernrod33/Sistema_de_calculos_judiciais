package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoIndice;

import java.time.LocalDate;

// DTO de resposta com os dados de um período de cálculo (datas e índice utilizado)
public record CalculoPeriodoResponse(
        Integer ordem,
        LocalDate dataInicial,
        LocalDate dataFinal,
        TipoIndice indiceUtilizado
) {
}