package br.com.ecogov.compliance.repositories;

import br.com.ecogov.compliance.models.LicencaAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LicencaAmbientalRepository extends JpaRepository<LicencaAmbiental, Long> {
    List<LicencaAmbiental> findByClienteIdCliente(Long idCliente);
    List<LicencaAmbiental> findByStatus(String status);

    @Query("SELECT l FROM LicencaAmbiental l WHERE l.dataValidade < :data")
    List<LicencaAmbiental> findLicencasExpiradas(LocalDate data);

    @Query("SELECT l FROM LicencaAmbiental l WHERE l.dataValidade BETWEEN :dataInicio AND :dataFim")
    List<LicencaAmbiental> findLicencasProximasDoVencimento(LocalDate dataInicio, LocalDate dataFim);
}