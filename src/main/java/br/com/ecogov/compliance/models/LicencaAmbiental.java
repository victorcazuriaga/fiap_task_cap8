package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "LICENCA_AMBIENTAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicencaAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_licenca")
    private Long idLicenca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Tipo de licença é obrigatório")
    @Column(name = "tipo_licenca", nullable = false, length = 50)
    private String tipoLicenca;

    @NotNull(message = "Data de emissão é obrigatória")
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @NotNull(message = "Data de validade é obrigatória")
    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @NotBlank(message = "Status é obrigatório")
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
