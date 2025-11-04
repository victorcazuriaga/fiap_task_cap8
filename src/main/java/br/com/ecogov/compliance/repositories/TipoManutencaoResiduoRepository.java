package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.TipoManutencaoResiduo;
import br.com.ecogov.compliance.models.TipoManutencaoResiduoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoManutencaoResiduoRepository extends JpaRepository<TipoManutencaoResiduo, TipoManutencaoResiduoId> {

    List<TipoManutencaoResiduo> findById_IdTipoManutencao(Integer idTipoManutencao);

    List<TipoManutencaoResiduo> findById_Residuo(Long residuo);
}