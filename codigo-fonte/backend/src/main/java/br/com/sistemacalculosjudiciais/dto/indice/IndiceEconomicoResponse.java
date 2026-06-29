package br.com.sistemacalculosjudiciais.dto.indice;

import br.com.sistemacalculosjudiciais.model.TipoIndice;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de resposta com os dados de um índice econômico cadastrado
public record IndiceEconomicoResponse(
        Long id,
        TipoIndice tipoIndice,
        LocalDate competencia,
        BigDecimal valorPercentual,
        LocalDate dataImportacao,
        String fonte
) {
}