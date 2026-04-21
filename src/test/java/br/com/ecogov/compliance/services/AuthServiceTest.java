package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.dtos.LoginRequestDTO;
import br.com.ecogov.compliance.dtos.LoginResponseDTO;
import br.com.ecogov.compliance.exceptions.ValidationException;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.models.ClientRole;
import br.com.ecogov.compliance.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private ClientDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = new ClientDTO();
        validDto.setNome("Empresa Teste");
        validDto.setCnpj("12345678000100");
        validDto.setPassword("senha123");
        validDto.setEmail("teste@empresa.com");
        validDto.setEndereco("Rua Teste, 123");
        validDto.setRole(ClientRole.USER);
    }

    @Test
    void register_shouldPersistAndReturnDto() {
        when(clientRepository.existsByCnpj("12345678000100")).thenReturn(false);
        when(clientRepository.existsByEmail("teste@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setIdCliente(UUID.randomUUID());
            return c;
        });

        ClientDTO result = authService.register(validDto);

        assertThat(result.getIdCliente()).isNotNull();
        assertThat(result.getNome()).isEqualTo("Empresa Teste");
        assertThat(result.getCnpj()).isEqualTo("12345678000100");
        assertThat(result.getAtivo()).isTrue();
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void register_shouldRejectDuplicateCnpj() {
        when(clientRepository.existsByCnpj("12345678000100")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CNPJ");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        when(clientRepository.existsByCnpj(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail("teste@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_shouldDefaultToUserRoleWhenNotProvided() {
        validDto.setRole(null);
        when(clientRepository.existsByCnpj(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientDTO result = authService.register(validDto);

        assertThat(result.getRole()).isEqualTo(ClientRole.USER);
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        Client client = new Client();
        client.setCnpj("12345678000100");
        client.setNome("Empresa Teste");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(client);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(tokenService.generateToken("12345678000100")).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(
                new LoginRequestDTO("12345678000100", "senha123"));

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getCnpj()).isEqualTo("12345678000100");
        assertThat(response.getNome()).isEqualTo("Empresa Teste");
    }

    @Test
    void login_shouldThrowValidationExceptionOnBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() ->
                authService.login(new LoginRequestDTO("12345678000100", "wrong")))
                .isInstanceOf(ValidationException.class);
    }
}
