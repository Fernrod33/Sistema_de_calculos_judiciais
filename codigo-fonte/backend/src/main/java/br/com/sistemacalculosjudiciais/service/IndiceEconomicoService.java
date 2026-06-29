package br.com.sistemacalculosjudiciais.service;

import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoRequest;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.dto.indice.ImportacaoSelicResponse;

import java.time.LocalDate;
import java.util.List;

// Interface de serviço que define as operações de gerenciamento e importação de índices econômicos
public interface IndiceEconomicoService {

    List<IndiceEconomicoResponse> listarTodos();

    IndiceEconomicoResponse buscarPorId(Long id);

    IndiceEconomicoResponse criar(IndiceEconomicoRequest request);

    IndiceEconomicoResponse atualizar(Long id, IndiceEconomicoRequest request);

    void excluir(Long id);

    List<IndiceEconomicoResponse> listarPorPeriodo(String tipoIndice, LocalDate competenciaInicial, LocalDate competenciaFinal);

    ImportacaoSelicResponse importarSelicPadrao();

    ImportacaoSelicResponse importarIpcaPadrao();

    ImportacaoSelicResponse importarIgpmPadrao();
}