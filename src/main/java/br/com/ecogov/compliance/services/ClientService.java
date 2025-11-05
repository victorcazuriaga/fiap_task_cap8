package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.exceptions.ResourceNotFoundException;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String cnpj) throws UsernameNotFoundException {
        return clientRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado: " + cnpj));
    }

    @Transactional(readOnly = true)
    public Page<ClientDTO> list(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
        return convertToDTO(client);
    }

    @Transactional(readOnly = true)
    public ClientDTO findByCnpj(String cnpj) {
        Client client = clientRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com CNPJ: " + cnpj));
        return convertToDTO(client);
    }

    @Transactional
    public ClientDTO update(UUID id, ClientDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        client.setNome(dto.getNome());
        client.setEndereco(dto.getEndereco());

        if (dto.getEmail() != null && !dto.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            client.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Client update = clientRepository.save(client);
        return convertToDTO(update);
    }

    @Transactional
    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    private ClientDTO convertToDTO(Client client) {
        return AuthService.getClientDTO(client);
    }


}