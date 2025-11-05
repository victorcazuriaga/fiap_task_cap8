package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.LicencaAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LicencaRepository extends JpaRepository<LicencaAmbiental, UUID> {
    List<LicencaAmbiental> findByClient_IdCliente(UUID idCliente);
}
