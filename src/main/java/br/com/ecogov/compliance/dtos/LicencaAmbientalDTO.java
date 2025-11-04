package br.com.ecogov.compliance.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicencaAmbientalDTO {
    private Long idLicenca;
    
    @NotNull(message = "ID do cliente é obrigatório")
    private Long idCliente;
    
    @NotBlank(message = "Tipo de licença é obrigatório")
    private String tipoLicenca;
    
    @NotNull(message = "Data de emissão é obrigatória")
    private LocalDate dataEmissao;
    
    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;
    
    @NotBlank(message = "Status é obrigatório")
    private String status;
}
