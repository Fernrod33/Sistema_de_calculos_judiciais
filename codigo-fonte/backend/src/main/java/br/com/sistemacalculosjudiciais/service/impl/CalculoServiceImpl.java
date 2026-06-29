package br.com.sistemacalculosjudiciais.service.impl;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoAtualizacaoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoReversoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoPeriodoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResumoResponse;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.exception.BusinessException;
import br.com.sistemacalculosjudiciais.exception.ResourceNotFoundException;
import br.com.sistemacalculosjudiciais.mapper.CalculoMapper;
import br.com.sistemacalculosjudiciais.model.Calculo;
import br.com.sistemacalculosjudiciais.model.CalculoMemoriaItem;
import br.com.sistemacalculosjudiciais.model.CalculoPeriodo;
import br.com.sistemacalculosjudiciais.model.IndiceEconomico;
import br.com.sistemacalculosjudiciais.model.TipoCalculo;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import br.com.sistemacalculosjudiciais.repository.CalculoRepository;
import br.com.sistemacalculosjudiciais.repository.IndiceEconomicoRepository;
import br.com.sistemacalculosjudiciais.service.CalculoService;
import br.com.sistemacalculosjudiciais.util.CalculoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

// Implementação do serviço de cálculo: executa atualização monetária, cálculo reverso e gerencia o histórico
@Service
@Transactional
@SuppressWarnings("null")
public class CalculoServiceImpl implements CalculoService {

    private final CalculoRepository calculoRepository;
    private final IndiceEconomicoRepository indiceRepository;

    public CalculoServiceImpl(CalculoRepository calculoRepository, IndiceEconomicoRepository indiceRepository) {
        this.calculoRepository = calculoRepository;
        this.indiceRepository = indiceRepository;
    }

    @Override
    public CalculoResultadoResponse calcularAtualizacaoMonetaria(CalculoAtualizacaoRequest request) {
        List<PeriodoNormalizado> periodos = normalizarPeriodos(request.dataInicial(), request.dataFinal(), request.indiceUtilizado(), request.periodos());
        List<CalculoMemoriaItem> memoria = gerarMemoriaCalculo(periodos, request.valorInicial(), false);
        BigDecimal valorFinal = memoria.isEmpty() ? request.valorInicial().setScale(8, RoundingMode.HALF_EVEN) : memoria.get(memoria.size() - 1).getValorCorrigido();
        Calculo calculo = salvarCalculo(TipoCalculo.ATUALIZACAO_MONETARIA, request.valorInicial(), valorFinal, request.nomeCalculo(), periodos, memoria);
        return toResultado(calculo);
    }

    @Override
    public CalculoResultadoResponse calcularValorHistorico(CalculoReversoRequest request) {
        List<PeriodoNormalizado> periodos = normalizarPeriodos(request.dataInicial(), request.dataFinal(), request.indiceUtilizado(), request.periodos());
        List<CalculoMemoriaItem> memoria = gerarMemoriaCalculo(periodos, request.valorAtual(), true);
        BigDecimal valorFinal = memoria.isEmpty() ? request.valorAtual().setScale(8, RoundingMode.HALF_EVEN) : memoria.get(memoria.size() - 1).getValorCorrigido();
        Calculo calculo = salvarCalculo(TipoCalculo.CALCULO_REVERSO, request.valorAtual(), valorFinal, request.nomeCalculo(), periodos, memoria);
        return toResultado(calculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalculoResumoResponse> listarCalculos() {
        return calculoRepository.findAllByOrderByDataCriacaoDesc().stream().map(CalculoMapper::toResumo).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalculoResponse buscarPorId(Long id) {
        return CalculoMapper.toResponse(encontrarCalculo(id));
    }

    @Override
    public void excluir(Long id) {
        calculoRepository.delete(encontrarCalculo(id));
    }

    @Override
    public List<CalculoMemoriaItem> gerarMemoriaCalculo(TipoIndice tipoIndice, LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorBase, boolean reverso) {
        return gerarMemoriaCalculoPeriodo(tipoIndice, dataInicial, dataFinal, valorBase, reverso);
    }

    private List<CalculoMemoriaItem> gerarMemoriaCalculo(List<PeriodoNormalizado> periodos, BigDecimal valorBase, boolean reverso) {
        List<PeriodoNormalizado> periodosOrdenados = new ArrayList<>(periodos);
        periodosOrdenados.sort(Comparator.comparing(PeriodoNormalizado::dataInicial));
        if (reverso) {
            Collections.reverse(periodosOrdenados);
        }

        BigDecimal valorAtual = valorBase.setScale(8, RoundingMode.HALF_EVEN);
        List<CalculoMemoriaItem> memoria = new ArrayList<>();
        for (PeriodoNormalizado periodo : periodosOrdenados) {
            List<CalculoMemoriaItem> memoriaPeriodo = gerarMemoriaCalculoPeriodo(
                    periodo.indiceUtilizado(),
                    periodo.dataInicial(),
                    periodo.dataFinal(),
                    valorAtual,
                    reverso
            );
            if (memoriaPeriodo.isEmpty()) {
                continue;
            }
            valorAtual = memoriaPeriodo.get(memoriaPeriodo.size() - 1).getValorCorrigido();
            memoria.addAll(memoriaPeriodo);
        }
        return memoria;
    }

    private List<CalculoMemoriaItem> gerarMemoriaCalculoPeriodo(TipoIndice tipoIndice, LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorBase, boolean reverso) {
        validarPeriodo(dataInicial, dataFinal);
        YearMonth inicio = YearMonth.from(dataInicial);
        YearMonth fim = YearMonth.from(dataFinal);
        List<IndiceEconomico> indices = new ArrayList<>(indiceRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(
            tipoIndice,
            inicio.atDay(1),
            fim.atEndOfMonth()));
        if (reverso) {
            Collections.reverse(indices);
        }
        if (indices.isEmpty()) {
            throw new BusinessException("Não há índices cadastrados para o período informado.");
        }

        BigDecimal valorAtual = valorBase.setScale(8, RoundingMode.HALF_EVEN);
        List<CalculoMemoriaItem> memoria = new ArrayList<>();
        for (IndiceEconomico indice : indices) {
            BigDecimal valorAnterior = valorAtual;
            valorAtual = reverso
                    ? CalculoUtils.aplicarIndiceReverso(valorAtual, indice.getValorPercentual())
                    : CalculoUtils.aplicarIndice(valorAtual, indice.getValorPercentual());
            memoria.add(CalculoMemoriaItem.builder()
                    .competencia(YearMonth.from(indice.getCompetencia()))
                    .indicePercentual(indice.getValorPercentual())
                    .valorAnterior(valorAnterior)
                    .valorCorrigido(valorAtual)
                    .build());
        }
        return memoria;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndiceEconomicoResponse> listarIndicesPeriodo(TipoIndice tipoIndice, LocalDate competenciaInicial, LocalDate competenciaFinal) {
        return indiceRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(tipoIndice, competenciaInicial, competenciaFinal)
                .stream()
                .map(indice -> new IndiceEconomicoResponse(indice.getId(), indice.getTipoIndice(), indice.getCompetencia(), indice.getValorPercentual(), indice.getDataImportacao(), indice.getFonte()))
                .toList();
    }

    private Calculo salvarCalculo(TipoCalculo tipoCalculo,
                                  BigDecimal valorOriginal,
                                  BigDecimal valorFinal,
                                  String nomeCalculo,
                                  List<PeriodoNormalizado> periodos,
                                  List<CalculoMemoriaItem> memoria) {
        List<PeriodoNormalizado> periodosOrdenados = new ArrayList<>(periodos);
        periodosOrdenados.sort(Comparator.comparing(PeriodoNormalizado::dataInicial));
        PeriodoNormalizado primeiroPeriodo = periodosOrdenados.get(0);
        PeriodoNormalizado ultimoPeriodo = periodosOrdenados.get(periodosOrdenados.size() - 1);
        Calculo calculo = Calculo.builder()
                .tipoCalculo(tipoCalculo)
                .valorOriginal(valorOriginal.setScale(8, RoundingMode.HALF_EVEN))
                .valorFinal(valorFinal.setScale(8, RoundingMode.HALF_EVEN))
                .indiceUtilizado(primeiroPeriodo.indiceUtilizado())
                .dataInicial(primeiroPeriodo.dataInicial())
                .dataFinal(ultimoPeriodo.dataFinal())
                .dataCriacao(LocalDateTime.now())
                .nomeCalculo(normalizarNomeCalculo(nomeCalculo))
                .build();

        List<CalculoPeriodo> periodosPersistidos = new ArrayList<>();
        for (int i = 0; i < periodosOrdenados.size(); i++) {
            PeriodoNormalizado periodo = periodosOrdenados.get(i);
            periodosPersistidos.add(CalculoPeriodo.builder()
                    .calculo(calculo)
                    .ordem(i + 1)
                    .dataInicial(periodo.dataInicial())
                    .dataFinal(periodo.dataFinal())
                    .indiceUtilizado(periodo.indiceUtilizado())
                    .build());
        }
        calculo.getPeriodos().addAll(periodosPersistidos);
        memoria.forEach(item -> item.setCalculo(calculo));
        calculo.getMemoria().addAll(memoria);
        return calculoRepository.save(calculo);
    }

    private br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse toResultado(Calculo calculo) {
        return new br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse(
                calculo.getId(),
                calculo.getNomeCalculo(),
                calculo.getTipoCalculo(),
                calculo.getIndiceUtilizado(),
                calculo.getValorOriginal(),
                calculo.getValorFinal(),
                calculo.getDataInicial(),
                calculo.getDataFinal(),
                calculo.getDataCriacao(),
                CalculoMapper.toPeriodosResponse(calculo),
                CalculoMapper.toMemoriaResponse(calculo.getMemoria())
        );
    }

    private Calculo encontrarCalculo(Long id) {
        return calculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cálculo não encontrado: " + id));
    }

    private void validarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new BusinessException("Informe datas válidas para o período.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new BusinessException("A data inicial não pode ser posterior à data final.");
        }
    }

    private List<PeriodoNormalizado> normalizarPeriodos(LocalDate dataInicial,
                                                        LocalDate dataFinal,
                                                        TipoIndice indiceUtilizado,
                                                        List<CalculoPeriodoRequest> periodos) {
        if (periodos != null && !periodos.isEmpty()) {
            List<PeriodoNormalizado> periodosNormalizados = new ArrayList<>();
            for (CalculoPeriodoRequest periodo : periodos) {
                if (periodo == null) {
                    continue;
                }
                validarPeriodo(periodo.dataInicial(), periodo.dataFinal());
                periodosNormalizados.add(new PeriodoNormalizado(periodo.dataInicial(), periodo.dataFinal(), periodo.indiceUtilizado()));
            }
            validarSobreposicao(periodosNormalizados);
            return periodosNormalizados;
        }

        if (dataInicial == null || dataFinal == null || indiceUtilizado == null) {
            throw new BusinessException("Informe ao menos um período de cálculo.");
        }

        validarPeriodo(dataInicial, dataFinal);
        return List.of(new PeriodoNormalizado(dataInicial, dataFinal, indiceUtilizado));
    }

    private void validarSobreposicao(List<PeriodoNormalizado> periodos) {
        if (periodos.isEmpty()) {
            throw new BusinessException("Informe ao menos um período de cálculo.");
        }

        List<PeriodoNormalizado> ordenados = new ArrayList<>(periodos);
        ordenados.sort(Comparator.comparing(PeriodoNormalizado::dataInicial));

        LocalDate fimAnterior = null;
        for (PeriodoNormalizado periodo : ordenados) {
            if (fimAnterior != null && periodo.dataInicial().isBefore(fimAnterior.plusDays(1))) {
                throw new BusinessException("Os períodos informados não podem se sobrepor.");
            }
            fimAnterior = periodo.dataFinal();
        }
    }

    private String normalizarNomeCalculo(String nomeCalculo) {
        if (nomeCalculo == null) {
            return null;
        }

        String nomeNormalizado = nomeCalculo.trim();
        return nomeNormalizado.isBlank() ? null : nomeNormalizado;
    }

    private record PeriodoNormalizado(LocalDate dataInicial, LocalDate dataFinal, TipoIndice indiceUtilizado) {
    }
}