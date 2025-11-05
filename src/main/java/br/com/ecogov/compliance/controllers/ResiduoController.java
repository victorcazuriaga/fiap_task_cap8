package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.ResiduoDTO;
import br.com.ecogov.compliance.services.ResiduoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/residuo")
@RequiredArgsConstructor
public class ResiduoController {

    private final ResiduoService residuoService;

    @PostMapping
    public ResponseEntity<ResiduoDTO> create(@RequestBody ResiduoDTO dto) {
        ResiduoDTO created = residuoService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ResiduoDTO>> listAll() {
        return ResponseEntity.ok(residuoService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResiduoDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(residuoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResiduoDTO> update(@PathVariable UUID id, @RequestBody ResiduoDTO dto) {
        return ResponseEntity.ok(residuoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        residuoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
