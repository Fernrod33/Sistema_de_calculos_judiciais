package br.com.sistemacalculosjudiciais.mapper;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoMemoriaResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoPeriodoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResumoResponse;
import br.com.sistemacalculosjudiciais.model.Calculo;
import br.com.sistemacalculosjudiciais.model.CalculoMemoriaItem;
import br.com.sistemacalculosjudiciais.model.CalculoPeriodo;

import java.util.List;

// Classe utilitária que converte entidades de Calculo em DTOs para as respostas da API
public final class CalculoMapper {

    private CalculoMapper() {
    }

    public static CalculoResumoResponse toResumo(Calculo calculo) {
        return new CalculoResumoResponse(
                calculo.getId(),
                calculo.getNomeCalculo(),
                calculo.getTipoCalculo(),
                calculo.getIndiceUtilizado(),
                calculo.getValorOriginal(),
                calculo.getValorFinal(),
                calculo.getDataInicial(),
                calculo.getDataFinal(),
                calculo.getDataCriacao()
        );
    }

    public static CalculoResponse toResponse(Calculo calculo) {
        return new CalculoResponse(
                calculo.getId(),
                calculo.getNomeCalculo(),
                calculo.getTipoCalculo(),
                calculo.getIndiceUtilizado(),
                calculo.getValorOriginal(),
                calculo.getValorFinal(),
                calculo.getDataInicial(),
                calculo.getDataFinal(),
                calculo.getDataCriacao(),
                toPeriodosResponse(calculo),
                calculo.getMemoria().stream().map(CalculoMapper::toMemoria).toList()
        );
    }

    public static CalculoMemoriaResponse toMemoria(CalculoMemoriaItem item) {
        return new CalculoMemoriaResponse(
                item.getCompetencia().toString(),
                item.getIndicePercentual(),
                item.getValorAnterior(),
                item.getValorCorrigido()
        );
    }

    public static List<CalculoMemoriaResponse> toMemoriaResponse(List<CalculoMemoriaItem> memoria) {
        return memoria.stream().map(CalculoMapper::toMemoria).toList();
    }

    public static List<CalculoPeriodoResponse> toPeriodosResponse(Calculo calculo) {
        if (calculo.getPeriodos() == null || calculo.getPeriodos().isEmpty()) {
            return List.of(new CalculoPeriodoResponse(
                    1,
                    calculo.getDataInicial(),
                    calculo.getDataFinal(),
                    calculo.getIndiceUtilizado()
            ));
        }

        return calculo.getPeriodos().stream().map(CalculoMapper::toPeriodo).toList();
    }

    private static CalculoPeriodoResponse toPeriodo(CalculoPeriodo periodo) {
        return new CalculoPeriodoResponse(
                periodo.getOrdem(),
                periodo.getDataInicial(),
                periodo.getDataFinal(),
                periodo.getIndiceUtilizado()
        );
    }
}