package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
    List<Manutencao> findByClienteIdCliente(Long idCliente);
    List<Manutencao> findByTipoManutencao(Integer tipoManutencao);

    @Query("SELECT m FROM Manutencao m WHERE m.dataManutencao BETWEEN :dataInicio AND :dataFim")
    List<Manutencao> findByPeriodo(LocalDate dataInicio, LocalDate dataFim);
}