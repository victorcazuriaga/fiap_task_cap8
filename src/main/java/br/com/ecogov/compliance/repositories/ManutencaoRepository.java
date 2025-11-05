package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {
    List<Manutencao> findByClient_IdCliente(UUID idCliente);
}
