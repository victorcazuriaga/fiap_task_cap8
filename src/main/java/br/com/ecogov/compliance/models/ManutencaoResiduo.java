package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "TIPO_MANUTENCAO_RESIDUO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManutencaoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manutencao", nullable = false)
    private Manutencao manutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_residuo", nullable = false)
    private Residuo residuo;

    @Column(name = "quantidade")
    private Double quantidade;
}
