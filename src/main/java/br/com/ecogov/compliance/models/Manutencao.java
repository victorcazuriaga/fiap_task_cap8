package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "MANUTENCAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_manutencao")
    private UUID idManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Client client;

    @Column(name = "tipo_manutencao")
    private Integer tipoManutencao;

    @Column(name = "data_manutencao")
    private LocalDate dataManutencao;

    @OneToMany(mappedBy = "manutencao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManutencaoResiduo> residuos = new ArrayList<>();
}
