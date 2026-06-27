package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoCalculo;
import br.com.sistemacalculosjudiciais.model.TipoIndice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// DTO de resposta completo de um cálculo, incluindo períodos e memória de cálculo detalhada
public record CalculoResponse(
        Long id,
        String nomeCalculo,
        TipoCalculo tipoCalculo,
        TipoIndice indiceUtilizado,
        BigDecimal valorOriginal,
        BigDecimal valorFinal,
        LocalDate dataInicial,
        LocalDate dataFinal,
        LocalDateTime dataCriacao,
        List<CalculoPeriodoResponse> periodos,
        List<CalculoMemoriaResponse> memoria
) {
}