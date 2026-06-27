package br.com.sistemacalculosjudiciais.dto.calculo;

import java.math.BigDecimal;

// DTO de resposta que representa um item da memória de cálculo por competência
public record CalculoMemoriaResponse(
        String competencia,
        BigDecimal indicePercentual,
        BigDecimal valorAnterior,
        BigDecimal valorCorrigido
) {
}