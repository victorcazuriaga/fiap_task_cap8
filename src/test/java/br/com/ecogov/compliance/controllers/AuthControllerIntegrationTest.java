package br.com.ecogov.compliance.controllers;

import br.com.ecogov.compliance.dtos.ClientDTO;
import br.com.ecogov.compliance.dtos.LoginRequestDTO;
import br.com.ecogov.compliance.models.ClientRole;
import br.com.ecogov.compliance.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    private ClientDTO newClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        newClient = new ClientDTO();
        newClient.setNome("Empresa Integração");
        newClient.setCnpj("11222333000144");
        newClient.setPassword("senha123");
        newClient.setEmail("integ@empresa.com");
        newClient.setEndereco("Av. Integração, 100");
        newClient.setRole(ClientRole.USER);
    }

    @Test
    void register_shouldCreateClientAndReturn201() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente", notNullValue()))
                .andExpect(jsonPath("$.cnpj").value("11222333000144"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void register_shouldReturn400WhenRequiredFieldsMissing() throws Exception {
        ClientDTO invalid = new ClientDTO();
        invalid.setCnpj("11222333000144");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400WhenCnpjAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnJwtTokenWhenCredentialsValid() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO("11222333000144", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.cnpj").value("11222333000144"));
    }

    @Test
    void login_shouldReturn400WhenPasswordWrong() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO("11222333000144", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }
}
