package br.com.sistemacalculosjudiciais.controller;

import br.com.sistemacalculosjudiciais.dto.calculo.CalculoAtualizacaoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoReversoRequest;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResultadoResponse;
import br.com.sistemacalculosjudiciais.dto.calculo.CalculoResumoResponse;
import br.com.sistemacalculosjudiciais.service.CalculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controller REST que expõe os endpoints de cálculo de atualização monetária e cálculo reverso
@RestController
@RequestMapping("/api/calculos")
public class CalculoController {

    private final CalculoService service;

    public CalculoController(CalculoService service) {
        this.service = service;
    }

    @PostMapping("/atualizacao")
    public ResponseEntity<CalculoResultadoResponse> calcularAtualizacao(@Valid @RequestBody CalculoAtualizacaoRequest request) {
        return ResponseEntity.ok(service.calcularAtualizacaoMonetaria(request));
    }

    @PostMapping("/reverso")
    public ResponseEntity<CalculoResultadoResponse> calcularReverso(@Valid @RequestBody CalculoReversoRequest request) {
        return ResponseEntity.ok(service.calcularValorHistorico(request));
    }

    @GetMapping
    public ResponseEntity<List<CalculoResumoResponse>> listar() {
        return ResponseEntity.ok(service.listarCalculos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}