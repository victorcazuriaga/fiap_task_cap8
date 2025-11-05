package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.LicencaDTO;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.models.LicencaAmbiental;
import br.com.ecogov.compliance.repositories.ClientRepository;
import br.com.ecogov.compliance.repositories.LicencaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicencaService {

    private final LicencaRepository licencaRepository;
    private final ClientRepository clientRepository;

    public LicencaDTO create(LicencaDTO dto) {
        Client client = clientRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        LicencaAmbiental licenca = new LicencaAmbiental();
        licenca.setClient(client);
        licenca.setTipoLicenca(dto.getTipoLicenca());
        licenca.setDataEmissao(dto.getDataEmissao());
        licenca.setDataValidade(dto.getDataValidade());
        licenca.setStatus(dto.getStatus());

        LicencaAmbiental saved = licencaRepository.save(licenca);
        dto.setIdLicenca(saved.getIdLicenca());
        return dto;
    }

    public LicencaDTO findById(UUID id) {
        LicencaAmbiental l = licencaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Licença não encontrada"));
        return toDto(l);
    }

    public List<LicencaDTO> listByClient(UUID clientId) {
        return licencaRepository.findByClient_IdCliente(clientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public LicencaDTO update(UUID id, LicencaDTO dto) {
        LicencaAmbiental l = licencaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Licença não encontrada"));
        if (dto.getTipoLicenca() != null) l.setTipoLicenca(dto.getTipoLicenca());
        if (dto.getDataEmissao() != null) l.setDataEmissao(dto.getDataEmissao());
        if (dto.getDataValidade() != null) l.setDataValidade(dto.getDataValidade());
        if (dto.getStatus() != null) l.setStatus(dto.getStatus());

        LicencaAmbiental saved = licencaRepository.save(l);
        return toDto(saved);
    }

    public void delete(UUID id) {
        licencaRepository.deleteById(id);
    }

    private LicencaDTO toDto(LicencaAmbiental l) {
        LicencaDTO dto = new LicencaDTO();
        dto.setIdLicenca(l.getIdLicenca());
        dto.setIdCliente(l.getClient().getIdCliente());
        dto.setTipoLicenca(l.getTipoLicenca());
        dto.setDataEmissao(l.getDataEmissao());
        dto.setDataValidade(l.getDataValidade());
        dto.setStatus(l.getStatus());
        return dto;
    }
}
