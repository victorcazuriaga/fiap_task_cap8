package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.LicencaDTO;
import br.com.ecogov.compliance.services.LicencaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/licenca")
@RequiredArgsConstructor
public class LicencaController {

    private final LicencaService licencaService;

    @PostMapping
    public ResponseEntity<LicencaDTO> create(@Valid @RequestBody LicencaDTO dto) {
        LicencaDTO created = licencaService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicencaDTO> findById(@PathVariable UUID id) {
        LicencaDTO dto = licencaService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<LicencaDTO>> listByClient(@PathVariable UUID clienteId) {
        List<LicencaDTO> list = licencaService.listByClient(clienteId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicencaDTO> update(@PathVariable UUID id, @RequestBody LicencaDTO dto) {
        LicencaDTO updated = licencaService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        licencaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
