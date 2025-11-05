package br.com.ecogov.compliance.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManutencaoResiduoDTO {
    private UUID id;
    private UUID idResiduo;
    private Double quantidade;
}
