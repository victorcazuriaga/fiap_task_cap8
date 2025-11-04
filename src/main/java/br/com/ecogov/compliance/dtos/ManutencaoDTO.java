package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManutencaoDTO {
    private Long idManutencao;
    
    @NotNull(message = "ID do cliente é obrigatório")
    private Long idCliente;
    
    @NotNull(message = "Tipo de manutenção é obrigatório")
    private Integer tipoManutencao;
    
    @NotNull(message = "Data da manutenção é obrigatória")
    private LocalDate dataManutencao;
}
