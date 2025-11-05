package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.dtos.LoginRequestDTO;
import br.com.ecogov.compliance.dtos.LoginResponseDTO;
import br.com.ecogov.compliance.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO dto) {
        ClientDTO data = authService.register(dto);
        System.out.println(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}