package br.com.sistemacalculosjudiciais.controller;

import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoRequest;
import br.com.sistemacalculosjudiciais.dto.indice.IndiceEconomicoResponse;
import br.com.sistemacalculosjudiciais.dto.indice.ImportacaoSelicResponse;
import br.com.sistemacalculosjudiciais.model.TipoIndice;
import br.com.sistemacalculosjudiciais.service.IndiceEconomicoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// Controller REST que expõe os endpoints de CRUD e importação de índices econômicos (SELIC, IPCA, IGP-M)
@RestController
@RequestMapping("/api/indices")
public class IndiceEconomicoController {

    private final IndiceEconomicoService service;

    public IndiceEconomicoController(IndiceEconomicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IndiceEconomicoResponse>> listar(
            @RequestParam(value = "tipoIndice", required = false) TipoIndice tipoIndice,
            @RequestParam(value = "dataInicial", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(value = "dataFinal", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        if (tipoIndice != null && dataInicial != null && dataFinal != null) {
            return ResponseEntity.ok(service.listarPorPeriodo(tipoIndice.name(), dataInicial, dataFinal));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndiceEconomicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IndiceEconomicoResponse> criar(@Valid @RequestBody IndiceEconomicoRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndiceEconomicoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody IndiceEconomicoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/importar-selic")
    public ResponseEntity<ImportacaoSelicResponse> importarSelic() {
        return ResponseEntity.ok(service.importarSelicPadrao());
    }

    @PostMapping("/importar-ipca")
    public ResponseEntity<ImportacaoSelicResponse> importarIpca() {
        return ResponseEntity.ok(service.importarIpcaPadrao());
    }

    @PostMapping("/importar-igpm")
    public ResponseEntity<ImportacaoSelicResponse> importarIgpm() {
        return ResponseEntity.ok(service.importarIgpmPadrao());
    }
}