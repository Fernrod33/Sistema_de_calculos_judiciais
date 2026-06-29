package br.com.sistemacalculosjudiciais.dto.indice;

import br.com.sistemacalculosjudiciais.model.TipoIndice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de requisição para criação ou atualização de um índice econômico
public record IndiceEconomicoRequest(
        @NotNull TipoIndice tipoIndice,
        @NotNull LocalDate competencia,
        @NotNull @DecimalMin("0.0") BigDecimal valorPercentual,
        @NotNull LocalDate dataImportacao,
        @NotNull @Size(max = 120) String fonte
) {
}