package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.Residuo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResiduoRepository extends JpaRepository<Residuo, Long> {
    Optional<Residuo> findByNomeResiduo(String nomeResiduo);

    @Query("SELECT r FROM Residuo r WHERE r.quantidadeAcumulada >= r.limiteReciclagem")
    List<Residuo> findResiduosAcimaDoLimite();

    @Query("SELECT r FROM Residuo r WHERE (r.quantidadeAcumulada / r.limiteReciclagem) >= :percentual")
    List<Residuo> findResiduosProximosDoLimite(double percentual);
}