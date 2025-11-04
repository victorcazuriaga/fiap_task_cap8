package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoManutencaoResiduoDTO {
    @NotNull(message = "ID do tipo de manutenção é obrigatório")
    private Integer idTipoManutencao;

    @NotNull(message = "ID do resíduo é obrigatório")
    private Long idResiduo;

    @NotNull(message = "Quantidade é obrigatória")
    private Double quantidade;
}