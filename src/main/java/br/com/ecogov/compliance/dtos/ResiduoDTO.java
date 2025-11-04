package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResiduoDTO {
    private Long idResiduo;
    
    @NotBlank(message = "Nome do resíduo é obrigatório")
    private String nomeResiduo;
    
    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unidadeMedida;
    
    @NotNull(message = "Quantidade acumulada é obrigatória")
    private Double quantidadeAcumulada;
    
    @NotNull(message = "Limite de reciclagem é obrigatório")
    private Double limiteReciclagem;
}
