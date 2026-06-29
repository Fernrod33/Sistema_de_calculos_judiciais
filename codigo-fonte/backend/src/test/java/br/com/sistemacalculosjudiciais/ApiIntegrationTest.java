package br.com.sistemacalculosjudiciais;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResumoResponse;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.model.TipoCalculo;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import br.com.sistemacalculosjudiciais.service.CalculoService;
import br.com.sistemacalculosjudiciais.service.IndiceEconomicoService;
import br.com.sistemacalculosjudiciais.service.RelatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculoService calculoService;

    @MockBean
    private IndiceEconomicoService indiceEconomicoService;

    @MockBean
    private RelatorioService relatorioService;

    @Test
    void deveCriarIndiceViaApi() throws Exception {
        when(indiceEconomicoService.criar(any())).thenReturn(new IndiceEconomicoResponse(1L, TipoIndice.SELIC, LocalDate.of(2024, 1, 1), new BigDecimal("0.52"), LocalDate.now(), "BACEN"));

        mockMvc.perform(post("/api/indices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoIndice":"SELIC","competencia":"2024-01-01","valorPercentual":0.52,"dataImportacao":"2024-01-10","fonte":"BACEN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void deveListarCalculosViaApi() throws Exception {
        when(calculoService.listarCalculos()).thenReturn(List.of(
            new CalculoResumoResponse(1L, "Cliente teste", TipoCalculo.ATUALIZACAO_MONETARIA, TipoIndice.SELIC, new BigDecimal("1000.00"), new BigDecimal("1015.98"), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/calculos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void deveBaixarRelatorioPdf() throws Exception {
        when(relatorioService.gerarPdf(1L)).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/relatorios/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF));
    }

    @Test
    void deveExcluirCalculoViaApi() throws Exception {
        mockMvc.perform(delete("/api/calculos/10"))
                .andExpect(status().isNoContent());
    }
}