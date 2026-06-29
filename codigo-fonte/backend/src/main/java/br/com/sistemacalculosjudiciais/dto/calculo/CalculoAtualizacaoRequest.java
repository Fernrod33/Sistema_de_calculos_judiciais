package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoIndice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// DTO de requisição para cálculo de atualização monetária, contendo valor inicial, índice e período
public record CalculoAtualizacaoRequest(
        @NotNull @DecimalMin("0.0") BigDecimal valorInicial,
        @Size(max = 120) String nomeCalculo,
        LocalDate dataInicial,
        LocalDate dataFinal,
        TipoIndice indiceUtilizado,
        List<CalculoPeriodoRequest> periodos
) {

        public CalculoAtualizacaoRequest(BigDecimal valorInicial, LocalDate dataInicial, LocalDate dataFinal, TipoIndice indiceUtilizado) {
                this(valorInicial, null, dataInicial, dataFinal, indiceUtilizado, null);
        }

        public CalculoAtualizacaoRequest(BigDecimal valorInicial, String nomeCalculo, LocalDate dataInicial, LocalDate dataFinal, TipoIndice indiceUtilizado) {
                this(valorInicial, nomeCalculo, dataInicial, dataFinal, indiceUtilizado, null);
        }
}