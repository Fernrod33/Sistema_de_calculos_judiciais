package br.com.sistemacalculosjudiciais.service.impl;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResponse;
import br.com.sistemacalculosjudiciais.exception.ResourceNotFoundException;
import br.com.sistemacalculosjudiciais.mapper.CalculoMapper;
import br.com.sistemacalculosjudiciais.model.Calculo;
import br.com.sistemacalculosjudiciais.repository.CalculoRepository;
import br.com.sistemacalculosjudiciais.service.RelatorioService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.awt.Color;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

// Implementação do serviço de relatórios: gera PDFs detalhados de cálculos individuais e consolidados
@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class RelatorioServiceImpl implements RelatorioService {

    private final CalculoRepository calculoRepository;

    public RelatorioServiceImpl(CalculoRepository calculoRepository) {
        this.calculoRepository = calculoRepository;
    }

    @Override
    public byte[] gerarPdf(Long calculoId) {
        Calculo calculo = calculoRepository.findById(calculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Cálculo não encontrado: " + calculoId));
        CalculoResponse response = CalculoMapper.toResponse(calculo);
        String titulo = response.nomeCalculo() == null || response.nomeCalculo().isBlank()
            ? "Relatório do cálculo #" + calculoId
            : "Relatório do cálculo: " + response.nomeCalculo();

        return gerarPdfIndividual(titulo, List.of(response));
    }

    @Override
    public byte[] gerarPdfConsolidado(List<Long> calculoIds) {
        if (calculoIds == null || calculoIds.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um cálculo para consolidar.");
        }

        List<CalculoResponse> calculos = new ArrayList<>();
        for (Long calculoId : calculoIds) {
            Calculo calculo = calculoRepository.findById(calculoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cálculo não encontrado: " + calculoId));
            calculos.add(CalculoMapper.toResponse(calculo));
        }

        return gerarPdfIndividual("Relatório consolidado", calculos);
    }

    private byte[] gerarPdfIndividual(String title, List<CalculoResponse> calculos) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 48, 48);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new FooterEvent());
            document.open();

            addTitle(document, title);
            addSeparator(document);
            addConsolidadoResumo(document, calculos);
            addSeparator(document);
            for (int i = 0; i < calculos.size(); i++) {
                CalculoResponse response = calculos.get(i);
                addResumo(document, response);
                addPeriodos(document, response.periodos());
                addMemoria(document, response);
                if (i < calculos.size() - 1) {
                    addSeparator(document);
                }
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao gerar PDF do relatório.", exception);
        }
    }

    private void addSeparator(Document document) throws DocumentException {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingBefore(8);
        PdfPCell sepCell = new PdfPCell(new Phrase(""));
        sepCell.setBorderWidthTop(3f);
        sepCell.setBorderWidthLeft(0f);
        sepCell.setBorderWidthRight(0f);
        sepCell.setBorderWidthBottom(0f);
        sepCell.setBorderColorTop(Color.BLACK);
        sepCell.setFixedHeight(10f);
        sepCell.setPadding(0);
        separator.addCell(sepCell);
        document.add(separator);
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        
        Font systemFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Paragraph systemName = new Paragraph("JurisCalc", systemFont);
        systemName.setAlignment(Element.ALIGN_CENTER);
        systemName.setSpacingAfter(6);
        document.add(systemName);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Data de emissão: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(12);
        document.add(subtitle);
    }

    private void addConsolidadoResumo(Document document, List<CalculoResponse> calculos) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8);
        table.addCell(cell("Valor inicial consolidado"));
        table.addCell(cell("Valor final consolidado"));
        table.setSpacingAfter(8);

        BigDecimal totalOriginal = calculos.stream()
                .map(CalculoResponse::valorOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalConsolidado = calculos.stream()
                .map(CalculoResponse::valorFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        table.addCell(cell(formatCurrency(totalOriginal)));
        table.addCell(cell(formatCurrency(totalConsolidado)));
        document.add(table);
    }

    private void addResumo(Document document, CalculoResponse response) throws DocumentException {
        String indicesUtilizados = response.periodos() == null || response.periodos().isEmpty()
            ? response.indiceUtilizado().name()
            : response.periodos().stream()
                .map(periodo -> periodo.indiceUtilizado().name())
                .distinct()
                .reduce((esquerda, direita) -> esquerda + ", " + direita)
                .orElse(response.indiceUtilizado().name());

        String identificacao = response.nomeCalculo() == null || response.nomeCalculo().isBlank()
                ? "#" + response.id()
                : response.nomeCalculo();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(8);
        table.addCell(cell("Identificação"));
        table.addCell(cell(identificacao));
        table.addCell(cell("Tipo"));
        table.addCell(cell(response.tipoCalculo().name()));
        table.addCell(cell("Índice utilizado"));
        table.addCell(cell(indicesUtilizados));
        table.addCell(cell("Data inicial"));
        table.addCell(cell(response.dataInicial().toString()));
        table.addCell(cell("Data final"));
        table.addCell(cell(response.dataFinal().toString()));
        table.addCell(cell("Valor inicial"));
        table.addCell(cell(formatCurrency(response.valorOriginal())));
        table.addCell(cell("Valor final"));
        table.addCell(cell(formatCurrency(response.valorFinal())));
        document.add(table);
    }

    private void addPeriodos(Document document, List<br.com.sistemacalculosjudiciais.dto.calculo.CalculoPeriodoResponse> periodos) throws DocumentException {
        Paragraph header = new Paragraph("Períodos aplicados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        header.setSpacingBefore(2);
        header.setSpacingAfter(2);
        document.add(header);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);
        table.setWidths(new float[]{0.9f, 1.7f, 1.7f, 1.2f});
        table.addCell(headerCell("Ordem"));
        table.addCell(headerCell("Data inicial"));
        table.addCell(headerCell("Data final"));
        table.addCell(headerCell("Índice"));

        periodos.forEach(periodo -> {
            table.addCell(cell(String.valueOf(periodo.ordem())));
            table.addCell(cell(periodo.dataInicial().toString()));
            table.addCell(cell(periodo.dataFinal().toString()));
            table.addCell(cell(periodo.indiceUtilizado().name()));
        });
        table.setSpacingAfter(8);
        document.add(table);
    }

    private void addMemoria(Document document, CalculoResponse response) throws DocumentException {
        Paragraph header = new Paragraph("Memória detalhada", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        header.setSpacingBefore(2);
        header.setSpacingAfter(2);
        document.add(header);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);
        table.setWidths(new float[]{2.2f, 1.4f, 1.6f, 1.6f});
        table.addCell(headerCell("Competência"));
        table.addCell(headerCell("Índice"));
        table.addCell(headerCell("Valor anterior"));
        table.addCell(headerCell("Valor corrigido"));

        response.memoria().forEach(item -> {
            table.addCell(cell(item.competencia().toString()));
            table.addCell(cell(item.indicePercentual().toPlainString() + "%"));
            table.addCell(cell(formatCurrency(item.valorAnterior())));
            table.addCell(cell(formatCurrency(item.valorCorrigido())));
        });
        table.setSpacingAfter(8);
        document.add(table);
    }

    private PdfPCell cell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = cell(text);
        cell.setBackgroundColor(new Color(230, 230, 230));
        cell.setPhrase(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        return cell;
    }

    private String formatCurrency(java.math.BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value);
    }

    private static class FooterEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();
                canvas.beginText();
                canvas.setFontAndSize(baseFont, 9);
                canvas.setTextMatrix(36, 28);
                canvas.showText("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                canvas.setTextMatrix(documentRight(document), 28);
                canvas.showText("Página " + writer.getPageNumber());
                canvas.endText();
            } catch (Exception ignored) {
            }
        }

        private float documentRight(Document document) {
            return document.getPageSize().getWidth() - 150;
        }
    }
}