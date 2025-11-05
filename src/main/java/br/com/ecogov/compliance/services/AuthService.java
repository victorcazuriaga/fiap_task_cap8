package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.dtos.LoginRequestDTO;
import br.com.ecogov.compliance.dtos.LoginResponseDTO;
import br.com.ecogov.compliance.exceptions.ValidationException;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.models.ClientRole;
import br.com.ecogov.compliance.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ClientDTO register(ClientDTO dto) {
        if (clientRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }

        if (dto.getEmail() != null && clientRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        System.out.println(dto);
        Client client = new Client();
        client.setNome(dto.getNome());
        client.setCnpj(dto.getCnpj());
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setEmail(dto.getEmail());
        client.setEndereco(dto.getEndereco());
        client.setRole(dto.getRole() != null ? dto.getRole() : ClientRole.USER);
        client.setAtivo(true);

        Client salvo = clientRepository.save(client);
        return converterParaDTO(salvo);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try{
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getCnpj(), dto.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);
        Client client = (Client) auth.getPrincipal();

        String token = tokenService.generateToken(client.getCnpj());

        return new LoginResponseDTO(token, "Bearer", client.getCnpj(), client.getNome());
        }catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new ValidationException("Verifique suas Credenciais");

        }
    }

    private ClientDTO converterParaDTO(Client client) {
        return getClientDTO(client);
    }

    static ClientDTO getClientDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setIdCliente(client.getIdCliente());
        dto.setNome(client.getNome());
        dto.setCnpj(client.getCnpj());
        dto.setEmail(client.getEmail());
        dto.setEndereco(client.getEndereco());
        dto.setRole(client.getRole());
        dto.setAtivo(client.getAtivo());
        return dto;
    }
}
