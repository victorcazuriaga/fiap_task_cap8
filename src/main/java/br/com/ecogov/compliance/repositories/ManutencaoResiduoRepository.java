package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.ManutencaoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManutencaoResiduoRepository extends JpaRepository<ManutencaoResiduo, UUID> {
    List<ManutencaoResiduo> findByManutencao_IdManutencao(UUID idManutencao);
}
