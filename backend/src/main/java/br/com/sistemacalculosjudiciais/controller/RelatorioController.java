package br.com.sistemacalculosjudiciais.controller;

import br.com.sistemacalculosjudiciais.service.RelatorioService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controller REST responsável pela geração de relatórios PDF individuais e consolidados
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        byte[] pdf = service.gerarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("relatorio-calculo-" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/consolidado/pdf")
    public ResponseEntity<byte[]> gerarPdfConsolidado(@RequestBody List<Long> ids) {
        byte[] pdf = service.gerarPdfConsolidado(ids);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("relatorio-consolidado.pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}