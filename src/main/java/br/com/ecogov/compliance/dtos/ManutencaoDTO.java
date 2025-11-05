package br.com.ecogov.compliance.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManutencaoDTO {
    private UUID idManutencao;
    private UUID idCliente;
    private Integer tipoManutencao;
    private LocalDate dataManutencao;
    private List<ManutencaoResiduoDTO> residuos;
}
