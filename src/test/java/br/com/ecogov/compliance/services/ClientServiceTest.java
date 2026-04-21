package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.exceptions.ResourceNotFoundException;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.models.ClientRole;
import br.com.ecogov.compliance.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private Client existing;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        existing = new Client();
        existing.setIdCliente(id);
        existing.setNome("Antigo");
        existing.setCnpj("12345678000100");
        existing.setEmail("old@empresa.com");
        existing.setEndereco("Rua Antiga");
        existing.setPassword("encoded");
        existing.setRole(ClientRole.USER);
        existing.setAtivo(true);
    }

    @Test
    void loadUserByUsername_shouldReturnUserWhenFound() {
        when(clientRepository.findByCnpj("12345678000100")).thenReturn(Optional.of(existing));

        UserDetails details = clientService.loadUserByUsername("12345678000100");

        assertThat(details.getUsername()).isEqualTo("12345678000100");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenNotFound() {
        when(clientRepository.findByCnpj("99999999000199")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.loadUserByUsername("99999999000199"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void findById_shouldReturnDtoWhenExists() {
        when(clientRepository.findById(id)).thenReturn(Optional.of(existing));

        ClientDTO dto = clientService.findById(id);

        assertThat(dto.getIdCliente()).isEqualTo(id);
        assertThat(dto.getNome()).isEqualTo("Antigo");
    }

    @Test
    void findById_shouldThrowWhenMissing() {
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldChangeNameAndAddress() {
        ClientDTO dto = new ClientDTO();
        dto.setNome("Novo Nome");
        dto.setEndereco("Rua Nova");

        when(clientRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientDTO result = clientService.update(id, dto);

        assertThat(result.getNome()).isEqualTo("Novo Nome");
        assertThat(result.getEndereco()).isEqualTo("Rua Nova");
    }

    @Test
    void update_shouldRejectDuplicateEmail() {
        ClientDTO dto = new ClientDTO();
        dto.setNome("Novo");
        dto.setEndereco("Rua Nova");
        dto.setEmail("new@empresa.com");

        when(clientRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmail("new@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> clientService.update(id, dto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void update_shouldEncodePasswordWhenProvided() {
        ClientDTO dto = new ClientDTO();
        dto.setNome("Novo");
        dto.setEndereco("Rua Nova");
        dto.setPassword("novaSenha");

        when(clientRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("novaSenha")).thenReturn("encoded-new");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        clientService.update(id, dto);

        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    void delete_shouldThrowWhenIdMissing() {
        when(clientRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> clientService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldRemoveWhenIdExists() {
        when(clientRepository.existsById(id)).thenReturn(true);

        clientService.delete(id);

        verify(clientRepository).deleteById(id);
    }
}
