package br.com.sistemacalculosjudiciais.mapper;

import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoRequest;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.model.IndiceEconomico;

// Classe utilitária que converte entidades de IndiceEconomico em DTOs e vice-versa
public final class IndiceEconomicoMapper {

    private IndiceEconomicoMapper() {
    }

    public static IndiceEconomico toEntity(IndiceEconomicoRequest request) {
        return IndiceEconomico.builder()
                .tipoIndice(request.tipoIndice())
                .competencia(request.competencia())
                .valorPercentual(request.valorPercentual())
                .dataImportacao(request.dataImportacao())
                .fonte(request.fonte())
                .build();
    }

    public static IndiceEconomicoResponse toResponse(IndiceEconomico entity) {
        return new IndiceEconomicoResponse(
                entity.getId(),
                entity.getTipoIndice(),
                entity.getCompetencia(),
                entity.getValorPercentual(),
                entity.getDataImportacao(),
                entity.getFonte()
        );
    }
}