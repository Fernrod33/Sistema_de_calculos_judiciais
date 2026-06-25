package br.com.sistemacalculosjudiciais.service;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoAtualizacaoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoReversoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResumoResponse;
import br.com.sistemacalculosjudiciais.model.CalculoMemoriaItem;
import br.com.sistemacalculosjudiciais.model.TipoIndice;

import java.time.LocalDate;
import java.util.List;

// Interface de serviço que define as operações de cálculo judicial disponíveis na aplicação
public interface CalculoService {

    CalculoResultadoResponse calcularAtualizacaoMonetaria(CalculoAtualizacaoRequest request);

    CalculoResultadoResponse calcularValorHistorico(CalculoReversoRequest request);

    List<CalculoResumoResponse> listarCalculos();

    CalculoResponse buscarPorId(Long id);

    void excluir(Long id);

    List<CalculoMemoriaItem> gerarMemoriaCalculo(TipoIndice tipoIndice, LocalDate dataInicial, LocalDate dataFinal, java.math.BigDecimal valorBase, boolean reverso);

    List<br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse> listarIndicesPeriodo(TipoIndice tipoIndice, LocalDate competenciaInicial, LocalDate competenciaFinal);
}