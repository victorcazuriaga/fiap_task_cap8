package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "TIPO_MANUTENCAO_RESIDUO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TipoManutencaoResiduoId.class)
public class TipoManutencaoResiduo {

    @Id
    @Column(name = "id_tipo_manutencao", nullable = false)
    private Integer idTipoManutencao;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_residuo", nullable = false)
    private Residuo residuo;

    @NotNull(message = "Quantidade é obrigatória")
    @Column(name = "quantidade", nullable = false)
    private Double quantidade;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoManutencaoResiduoId implements Serializable {
    private Integer idTipoManutencao;
    private Long residuo;
}
