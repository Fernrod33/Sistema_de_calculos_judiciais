package br.com.sistemacalculosjudiciais.service.impl;

import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoRequest;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.dto.indice.ImportacaoSelicResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.sistemacalculosjudiciais.exception.ResourceNotFoundException;
import br.com.sistemacalculosjudiciais.mapper.IndiceEconomicoMapper;
import br.com.sistemacalculosjudiciais.model.IndiceEconomico;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import br.com.sistemacalculosjudiciais.repository.IndiceEconomicoRepository;
import br.com.sistemacalculosjudiciais.service.IndiceEconomicoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Implementação do serviço de índices: realiza CRUD e importa dados de SELIC, IPCA e IGP-M de fontes externas
@Service
@Transactional
@SuppressWarnings("null")
public class IndiceEconomicoServiceImpl implements IndiceEconomicoService {

    
    private static final String SELIC_SOURCE_URL = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.4390/dados?formato=json";
    
    private static final String IPCA_SOURCE_URL = "https://www.dadosdemercado.com.br/indices/ipca";
    
    private static final String IGPM_SOURCE_URL = "https://www.dadosdemercado.com.br/indices/igp-m";

    private static final DateTimeFormatter BCB_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IndiceEconomicoRepository repository;
    private final ObjectMapper objectMapper;

    public IndiceEconomicoServiceImpl(IndiceEconomicoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndiceEconomicoResponse> listarTodos() {
        return repository.findAll().stream().map(IndiceEconomicoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IndiceEconomicoResponse buscarPorId(Long id) {
        return IndiceEconomicoMapper.toResponse(encontrarEntidade(id));
    }

    @Override
    public IndiceEconomicoResponse criar(IndiceEconomicoRequest request) {
        IndiceEconomico entity = IndiceEconomicoMapper.toEntity(request);
        return IndiceEconomicoMapper.toResponse(repository.save(entity));
    }

    @Override
    public IndiceEconomicoResponse atualizar(Long id, IndiceEconomicoRequest request) {
        IndiceEconomico entity = encontrarEntidade(id);
        entity.setTipoIndice(request.tipoIndice());
        entity.setCompetencia(request.competencia());
        entity.setValorPercentual(request.valorPercentual());
        entity.setDataImportacao(request.dataImportacao());
        entity.setFonte(request.fonte());
        return IndiceEconomicoMapper.toResponse(repository.save(entity));
    }

    @Override
    public void excluir(Long id) {
        repository.delete(encontrarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndiceEconomicoResponse> listarPorPeriodo(String tipoIndice, LocalDate competenciaInicial, LocalDate competenciaFinal) {
        TipoIndice indice = TipoIndice.valueOf(tipoIndice);
        return repository.findByTipoIndiceAndCompetenciaBetweenOrderByCompetenciaAsc(indice, competenciaInicial, competenciaFinal)
                .stream().map(IndiceEconomicoMapper::toResponse).toList();
    }

    @Override
    public ImportacaoSelicResponse importarSelicPadrao() {
        return importarIndicePadrao(
                TipoIndice.SELIC,
                SELIC_SOURCE_URL,
                buscarIndicesSelic()
        );
    }

    @Override
    public ImportacaoSelicResponse importarIpcaPadrao() {
        return importarIndicePadrao(
                TipoIndice.IPCA,
                IPCA_SOURCE_URL,
                buscarIndicesIpca()
        );
    }

    @Override
    public ImportacaoSelicResponse importarIgpmPadrao() {
        return importarIndicePadrao(
                TipoIndice.IGPM,
                IGPM_SOURCE_URL,
                buscarIndicesIgpm()
        );
    }

    private ImportacaoSelicResponse importarIndicePadrao(TipoIndice tipoIndice, String sourceUrl, List<IndiceEconomico> indices) {
        
        
        repository.deleteByTipoIndice(tipoIndice);
        repository.flush();
        repository.saveAllAndFlush(indices);

        
        long registrosPersistidos = repository.countByTipoIndice(tipoIndice);

        
        return new ImportacaoSelicResponse(
                tipoIndice.name(),
                indices.size(),
                Math.toIntExact(registrosPersistidos),
                sourceUrl,
                registrosPersistidos == indices.size()
        );
    }

    private List<IndiceEconomico> buscarIndicesSelic() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(SELIC_SOURCE_URL)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Resposta inválida ao consultar a SELIC no Banco Central.");
            }
            return parseSelicJson(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Falha ao importar a tabela SELIC do Banco Central.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao importar a tabela SELIC do Banco Central.", exception);
        }
    }

    private List<IndiceEconomico> buscarIndicesIpca() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(IPCA_SOURCE_URL)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Resposta inválida ao consultar o IPCA no site Dados de Mercado.");
            }
            return parseIpcaHtml(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Falha ao importar a tabela IPCA do site Dados de Mercado.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao importar a tabela IPCA do site Dados de Mercado.", exception);
        }
    }

    private List<IndiceEconomico> buscarIndicesIgpm() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(IGPM_SOURCE_URL)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Resposta inválida ao consultar o IGP-M no site Dados de Mercado.");
            }
            return parseIgpmHtml(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Falha ao importar a tabela IGP-M do site Dados de Mercado.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao importar a tabela IGP-M do site Dados de Mercado.", exception);
        }
    }

    List<IndiceEconomico> parseSelicJson(String json) {
        List<IndiceEconomico> indices = new ArrayList<>();

        try {
            List<BcbSerieItem> serie = objectMapper.readValue(json, new TypeReference<List<BcbSerieItem>>() {});
            for (BcbSerieItem item : serie) {
                LocalDate competencia = LocalDate.parse(item.data(), BCB_DATE_FORMAT);
                indices.add(IndiceEconomico.builder()
                        .tipoIndice(TipoIndice.SELIC)
                        .competencia(competencia.withDayOfMonth(1))
                        .valorPercentual(parsePercentual(item.valor()))
                        .dataImportacao(LocalDate.now())
                        .fonte("Banco Central do Brasil - SGS 4390")
                        .build());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao interpretar a série SELIC retornada pelo Banco Central.", exception);
        }

        return indices;
    }

    List<IndiceEconomico> parseIpcaHtml(String html) {
        return parseIndiceMensalHtml(html, TipoIndice.IPCA, "Dados de Mercado - https://www.dadosdemercado.com.br/indices/ipca");
    }

    List<IndiceEconomico> parseIgpmHtml(String html) {
        return parseIndiceMensalHtml(html, TipoIndice.IGPM, "Dados de Mercado - https://www.dadosdemercado.com.br/indices/igp-m");
    }

    private List<IndiceEconomico> parseIndiceMensalHtml(String html, TipoIndice tipoIndice, String fonte) {
        
        
        
        Document document = Jsoup.parse(html);
        Elements rows = document.select("table tbody tr");
        List<IndiceEconomico> indices = new ArrayList<>();

        for (Element row : rows) {
            Elements cells = row.select("th,td");
            if (cells.size() < 2) {
                continue;
            }

            String firstCell = cells.get(0).text().trim();
            if (!firstCell.matches("[A-Za-z]{3}/\\d{4}")) {
                continue;
            }

            YearMonth competencia = parseCompetenciaMesAno(firstCell);
            String valorMensal = cells.get(1).text().trim();
            if (valorMensal.isBlank() || "-".equals(valorMensal) || "--".equals(valorMensal)) {
                continue;
            }

            indices.add(IndiceEconomico.builder()
                    .tipoIndice(tipoIndice)
                    .competencia(competencia.atDay(1))
                    .valorPercentual(parsePercentual(valorMensal))
                    .dataImportacao(LocalDate.now())
                    .fonte(fonte)
                    .build());
        }

        return indices;
    }

    private YearMonth parseCompetenciaMesAno(String value) {
        String[] parts = value.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Competência inválida: " + value);
        }

        int month = switch (parts[0].toLowerCase()) {
            case "jan" -> 1;
            case "fev" -> 2;
            case "mar" -> 3;
            case "abr" -> 4;
            case "mai" -> 5;
            case "jun" -> 6;
            case "jul" -> 7;
            case "ago" -> 8;
            case "set" -> 9;
            case "out" -> 10;
            case "nov" -> 11;
            case "dez" -> 12;
            default -> throw new IllegalArgumentException("Mês inválido: " + parts[0]);
        };

        return YearMonth.of(Integer.parseInt(parts[1]), month);
    }

    BigDecimal parsePercentual(String value) {
        String normalized = value.replace("%", "").trim();
        if (normalized.contains(",")) {
            return new BigDecimal(normalized.replace(".", "").replace(",", "."));
        }

        return new BigDecimal(normalized);
    }

    private IndiceEconomico encontrarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Índice econômico não encontrado: " + id));
    }

    private record BcbSerieItem(String data, String valor) {
    }
}