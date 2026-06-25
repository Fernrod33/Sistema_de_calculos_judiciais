package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoCalculo;
import br.com.sistemacalculosjudiciais.model.TipoIndice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de resposta resumido de um cálculo, sem memória detalhada, usado para listagens
public record CalculoResumoResponse(
        Long id,
        String nomeCalculo,
        TipoCalculo tipoCalculo,
        TipoIndice indiceUtilizado,
        BigDecimal valorOriginal,
        BigDecimal valorFinal,
        LocalDate dataInicial,
        LocalDate dataFinal,
        LocalDateTime dataCriacao
) {
}