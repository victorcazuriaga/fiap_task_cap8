package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.services.ClientService;
import br.com.ecogov.compliance.services.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final TokenService tokenService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClientDTO>> list(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        Page<ClientDTO> clients = clientService.list(pageable);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findById(@PathVariable UUID id) {
        ClientDTO client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }
    @GetMapping("profile")
    public ResponseEntity<ClientDTO> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        String cnpj = tokenService.validateToken(token);
        ClientDTO client = clientService.findByCnpj(cnpj);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<ClientDTO> findByCnpj(@PathVariable String cnpj) {
        ClientDTO client = clientService.findByCnpj(cnpj);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClientDTO dto) {
        ClientDTO update = clientService.update(id, dto);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
