package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicencaDTO {
    private UUID idLicenca;

    @NotNull(message = "idCliente é obrigatório")
    private UUID idCliente;

    private String tipoLicenca;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private String status;
}
