package br.com.sistemacalculosjudiciais.service.impl;

import br.com.sistemacalculosjudiciais.model.IndiceEconomico;
import br.com.sistemacalculosjudiciais.repository.IndiceEconomicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
// @SuppressWarnings("null")
class IndiceEconomicoServiceImplTest {

    @Mock
    private IndiceEconomicoRepository repository;

    private IndiceEconomicoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new IndiceEconomicoServiceImpl(repository, new ObjectMapper());
    }

    @Test
    void deveParsearSerieSelicDoBancoCentral() {
        String json = """
                [
                    {"data":"01/08/1986","valor":"2.57"},
                    {"data":"01/09/1986","valor":"2.94"}
                ]
                """;

        List<IndiceEconomico> indices = service.parseSelicJson(json);

        assertThat(indices).hasSize(2);
        assertThat(indices.get(0).getCompetencia()).isEqualTo(java.time.LocalDate.of(1986, 8, 1));
        assertThat(indices.get(0).getValorPercentual()).isEqualByComparingTo(new BigDecimal("2.57"));
        assertThat(indices.get(0).getFonte()).isEqualTo("Banco Central do Brasil - SGS 4390");
        assertThat(indices.get(1).getCompetencia()).isEqualTo(java.time.LocalDate.of(1986, 9, 1));
        assertThat(indices.get(1).getValorPercentual()).isEqualByComparingTo(new BigDecimal("2.94"));
    }

    @Test
    void deveParsearTabelaIpcaComCompetenciaMensal() {
        String html = """
                <table>
                    <tbody>
                        <tr>
                            <td>Abr/2026</td>
                            <td>0,67</td>
                            <td>2,60</td>
                            <td>4,39</td>
                        </tr>
                        <tr>
                            <td>Mar/2026</td>
                            <td>0,88</td>
                            <td>1,92</td>
                            <td>4,14</td>
                        </tr>
                    </tbody>
                </table>
                """;

        List<IndiceEconomico> indices = service.parseIpcaHtml(html);

        assertThat(indices).hasSize(2);
        assertThat(indices.get(0).getTipoIndice()).isEqualTo(br.com.sistemacalculosjudiciais.model.TipoIndice.IPCA);
        assertThat(indices.get(0).getCompetencia()).isEqualTo(java.time.LocalDate.of(2026, 4, 1));
        assertThat(indices.get(0).getValorPercentual()).isEqualByComparingTo(new BigDecimal("0.67"));
        assertThat(indices.get(1).getCompetencia()).isEqualTo(java.time.LocalDate.of(2026, 3, 1));
        assertThat(indices.get(1).getValorPercentual()).isEqualByComparingTo(new BigDecimal("0.88"));
    }

    @Test
    void deveParsearTabelaIgpmComCompetenciaMensal() {
        String html = """
                <table>
                    <tbody>
                        <tr>
                            <td>Mai/2026</td>
                            <td>0,84</td>
                            <td>3,80</td>
                            <td>1,96</td>
                        </tr>
                        <tr>
                            <td>Abr/2026</td>
                            <td>2,73</td>
                            <td>2,93</td>
                            <td>0,62</td>
                        </tr>
                    </tbody>
                </table>
                """;

        List<IndiceEconomico> indices = service.parseIgpmHtml(html);

        assertThat(indices).hasSize(2);
        assertThat(indices.get(0).getTipoIndice()).isEqualTo(br.com.sistemacalculosjudiciais.model.TipoIndice.IGPM);
        assertThat(indices.get(0).getCompetencia()).isEqualTo(java.time.LocalDate.of(2026, 5, 1));
        assertThat(indices.get(0).getValorPercentual()).isEqualByComparingTo(new BigDecimal("0.84"));
        assertThat(indices.get(1).getCompetencia()).isEqualTo(java.time.LocalDate.of(2026, 4, 1));
        assertThat(indices.get(1).getValorPercentual()).isEqualByComparingTo(new BigDecimal("2.73"));
    }
}
