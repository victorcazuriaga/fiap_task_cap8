package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.ManutencaoDTO;
import br.com.ecogov.compliance.services.ManutencaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

    private final ManutencaoService manutencaoService;

    @PostMapping
    public ResponseEntity<ManutencaoDTO> create(@RequestBody ManutencaoDTO dto) {
        return ResponseEntity.ok(manutencaoService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManutencaoDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(manutencaoService.findById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ManutencaoDTO>> listByClient(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(manutencaoService.listByClient(clienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        manutencaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
