package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long idCliente;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;
    
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
}
