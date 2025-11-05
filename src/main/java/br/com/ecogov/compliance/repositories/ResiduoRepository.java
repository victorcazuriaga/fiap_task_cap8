package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.Residuo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResiduoRepository extends JpaRepository<Residuo, UUID> {
}
