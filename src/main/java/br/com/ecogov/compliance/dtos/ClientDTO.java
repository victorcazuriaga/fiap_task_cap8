package br.com.ecogov.compliance.dtos;

import br.com.ecogov.compliance.models.ClientRole;
import br.com.ecogov.compliance.models.ClientRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private UUID idCliente;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    private String email;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    private ClientRole role;
    private Boolean ativo;
}