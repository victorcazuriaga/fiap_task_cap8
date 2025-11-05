package br.com.ecogov.compliance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "RESIDUO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Residuo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_residuo")
    private UUID idResiduo;

    @Column(name = "nome_residuo", length = 50)
    private String nomeResiduo;

    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida;

    @Column(name = "quantidade_acumulada")
    private Double quantidadeAcumulada;

    @Column(name = "limite_reciclagem")
    private Double limiteReciclagem;
}
