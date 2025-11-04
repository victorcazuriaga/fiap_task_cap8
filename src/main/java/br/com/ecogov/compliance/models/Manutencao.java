package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "MANUTENCAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manutencao")
    private Long idManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull(message = "Tipo de manutenção é obrigatório")
    @Column(name = "tipo_manutencao", nullable = false)
    private Integer tipoManutencao;

    @NotNull(message = "Data da manutenção é obrigatória")
    @Column(name = "data_manutencao", nullable = false)
    private LocalDate dataManutencao;
}
