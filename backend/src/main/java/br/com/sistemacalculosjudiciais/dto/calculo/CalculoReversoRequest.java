package br.com.sistemacalculosjudiciais.dto.calculo;

import br.com.sistemacalculosjudiciais.model.TipoIndice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// DTO de requisição para cálculo reverso, determinando o valor histórico a partir do valor atual
public record CalculoReversoRequest(
        @NotNull @DecimalMin("0.0") BigDecimal valorAtual,
        @Size(max = 120) String nomeCalculo,
        LocalDate dataInicial,
        LocalDate dataFinal,
        TipoIndice indiceUtilizado,
        List<CalculoPeriodoRequest> periodos
) {

        public CalculoReversoRequest(BigDecimal valorAtual, LocalDate dataInicial, LocalDate dataFinal, TipoIndice indiceUtilizado) {
                this(valorAtual, null, dataInicial, dataFinal, indiceUtilizado, null);
        }

        public CalculoReversoRequest(BigDecimal valorAtual, String nomeCalculo, LocalDate dataInicial, LocalDate dataFinal, TipoIndice indiceUtilizado) {
                this(valorAtual, nomeCalculo, dataInicial, dataFinal, indiceUtilizado, null);
        }
}