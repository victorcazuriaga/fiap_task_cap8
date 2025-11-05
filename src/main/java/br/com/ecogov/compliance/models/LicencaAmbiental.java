package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "LICENCA_AMBIENTAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicencaAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_licenca")
    private UUID idLicenca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Client client;

    @Column(name = "tipo_licenca", length = 50)
    private String tipoLicenca;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "status", length = 20)
    private String status;
}
