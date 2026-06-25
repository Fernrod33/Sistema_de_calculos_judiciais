package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoIndice;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// DTO de requisição para definir um período de cálculo com índice específico
public record CalculoPeriodoRequest(
        @NotNull LocalDate dataInicial,
        @NotNull LocalDate dataFinal,
        @NotNull TipoIndice indiceUtilizado
) {
}