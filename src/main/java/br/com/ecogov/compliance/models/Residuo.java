package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESIDUO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Residuo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_residuo")
    private Long idResiduo;

    @NotBlank(message = "Nome do resíduo é obrigatório")
    @Column(name = "nome_residuo", nullable = false, length = 50)
    private String nomeResiduo;

    @NotBlank(message = "Unidade de medida é obrigatória")
    @Column(name = "unidade_medida", nullable = false, length = 10)
    private String unidadeMedida;

    @NotNull(message = "Quantidade acumulada é obrigatória")
    @Column(name = "quantidade_acumulada", nullable = false)
    private Double quantidadeAcumulada;

    @NotNull(message = "Limite de reciclagem é obrigatório")
    @Column(name = "limite_reciclagem", nullable = false)
    private Double limiteReciclagem;

    @OneToMany(mappedBy = "residuo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoManutencaoResiduo> tiposManutencao = new ArrayList<>();
}
