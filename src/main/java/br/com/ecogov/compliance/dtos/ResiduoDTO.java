package br.com.ecogov.compliance.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResiduoDTO {
    private UUID idResiduo;
    private String nomeResiduo;
    private String unidadeMedida;
    private Double quantidadeAcumulada;
    private Double limiteReciclagem;
}
