package br.com.sistemacalculosjudiciais.dto.indice;

// DTO de resposta com estatísticas de uma operação de importação de índices econômicos
public record ImportacaoSelicResponse(
        String tipoIndice,
        int registrosImportados,
        int registrosPersistidos,
        String origem,
        boolean persistido
) {
}