package br.com.sistemacalculosjudiciais.service.impl;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoAtualizacaoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoReversoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoPeriodoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse;
import br.com.sistemacalculosjudiciais.model.Calculo;
import br.com.sistemacalculosjudiciais.model.IndiceEconomico;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import br.com.sistemacalculosjudiciais.repository.CalculoRepository;
import br.com.sistemacalculosjudiciais.repository.IndiceEconomicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class CalculoServiceImplTest {

    @Mock
    private CalculoRepository calculoRepository;

    @Mock
    private IndiceEconomicoRepository indiceEconomicoRepository;

    @InjectMocks
    private CalculoServiceImpl service;

    @Test
    void deveCalcularAtualizacaoMonetariaMesAMes() {
        when(indiceEconomicoRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(
                TipoIndice.SELIC,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 31)
        )).thenReturn(List.of(
                indice(YearMonthHelper.date(2024, 1), new BigDecimal("0.52")),
                indice(YearMonthHelper.date(2024, 2), new BigDecimal("0.67")),
                indice(YearMonthHelper.date(2024, 3), new BigDecimal("0.40"))
        ));
        when(calculoRepository.save(any(Calculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CalculoResultadoResponse response = service.calcularAtualizacaoMonetaria(new CalculoAtualizacaoRequest(
                new BigDecimal("1000.00"),
            "Cliente teste",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 31),
                TipoIndice.SELIC
        ));

        assertThat(response.valorFinal()).isEqualByComparingTo("1015.98257936");
        assertThat(response.nomeCalculo()).isEqualTo("Cliente teste");
        assertThat(response.memoria()).hasSize(3);
        assertThat(response.memoria().get(0).valorCorrigido()).isEqualByComparingTo("1005.20000000");
    }

        @Test
        void deveCalcularAtualizacaoComMultiplosPeriodos() {
        when(indiceEconomicoRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(
            TipoIndice.SELIC,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 29)
        )).thenReturn(List.of(
            indice(YearMonthHelper.date(2024, 1), new BigDecimal("0.52")),
            indice(YearMonthHelper.date(2024, 2), new BigDecimal("0.67"))
        ));
        when(indiceEconomicoRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(
            TipoIndice.IPCA,
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 3, 31)
        )).thenReturn(List.of(
            indice(YearMonthHelper.date(2024, 3), new BigDecimal("0.29"))
        ));
        when(calculoRepository.save(any(Calculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CalculoResultadoResponse response = service.calcularAtualizacaoMonetaria(new CalculoAtualizacaoRequest(
            new BigDecimal("1000.00"),
            null,
            null,
            null,
            null,
            List.of(
                new CalculoPeriodoRequest(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 29), TipoIndice.SELIC),
                new CalculoPeriodoRequest(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31), TipoIndice.IPCA)
            )
        ));

        assertThat(response.periodos()).hasSize(2);
        assertThat(response.periodos()).extracting(periodo -> periodo.indiceUtilizado()).containsExactly(TipoIndice.SELIC, TipoIndice.IPCA);
        assertThat(response.memoria()).hasSize(3);
        }

        @Test
        void deveCalcularReversoPartindoDaDataFinal() {
        when(indiceEconomicoRepository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(
            TipoIndice.SELIC,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 31)
        )).thenReturn(List.of(
            indice(YearMonthHelper.date(2024, 1), new BigDecimal("0.52")),
            indice(YearMonthHelper.date(2024, 2), new BigDecimal("0.67")),
            indice(YearMonthHelper.date(2024, 3), new BigDecimal("0.40"))
        ));
        when(calculoRepository.save(any(Calculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CalculoResultadoResponse response = service.calcularValorHistorico(new CalculoReversoRequest(
            new BigDecimal("1000.00"),
            null,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 31),
            TipoIndice.SELIC
        ));

        assertThat(response.memoria()).hasSize(3);
        assertThat(response.memoria().get(0).competencia()).isEqualTo("2024-03");
        assertThat(response.memoria().get(2).competencia()).isEqualTo("2024-01");
        }

    private IndiceEconomico indice(LocalDate competencia, BigDecimal percentual) {
        return IndiceEconomico.builder()
                .tipoIndice(TipoIndice.SELIC)
                .competencia(competencia)
                .valorPercentual(percentual)
                .dataImportacao(LocalDate.now())
                .fonte("BACEN")
                .build();
    }

    private static final class YearMonthHelper {
        static LocalDate date(int year, int month) {
            return LocalDate.of(year, month, 1);
        }
    }
}