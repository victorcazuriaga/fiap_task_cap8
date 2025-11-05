package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ManutencaoDTO;
import br.com.ecogov.compliance.dtos.ManutencaoResiduoDTO;
import br.com.ecogov.compliance.models.Client;
import br.com.ecogov.compliance.models.Manutencao;
import br.com.ecogov.compliance.models.ManutencaoResiduo;
import br.com.ecogov.compliance.models.Residuo;
import br.com.ecogov.compliance.repositories.ClientRepository;
import br.com.ecogov.compliance.repositories.ManutencaoRepository;
import br.com.ecogov.compliance.repositories.ManutencaoResiduoRepository;
import br.com.ecogov.compliance.repositories.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final ClientRepository clientRepository;
    private final ResiduoRepository residuoRepository;
    private final ManutencaoResiduoRepository manutencaoResiduoRepository;

    public ManutencaoDTO create(ManutencaoDTO dto) {
        Client client = clientRepository.findById(dto.getIdCliente()).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Manutencao m = new Manutencao();
        m.setClient(client);
        m.setTipoManutencao(dto.getTipoManutencao());
        m.setDataManutencao(dto.getDataManutencao());

        Manutencao saved = manutencaoRepository.save(m);

        if (dto.getResiduos() != null) {
            final Manutencao manutSaved = saved;
            List<ManutencaoResiduo> itens = dto.getResiduos().stream().map(rdto -> {
                Residuo res = residuoRepository.findById(rdto.getIdResiduo()).orElseThrow(() -> new IllegalArgumentException("Residuo não encontrado"));
                ManutencaoResiduo mr = new ManutencaoResiduo();
                mr.setManutencao(manutSaved);
                mr.setResiduo(res);
                mr.setQuantidade(rdto.getQuantidade());
                return manutencaoResiduoRepository.save(mr);
            }).collect(Collectors.toList());
            saved.setResiduos(itens);
            saved = manutencaoRepository.save(saved);
        }

        dto.setIdManutencao(saved.getIdManutencao());
        return dto;
    }

    public ManutencaoDTO findById(UUID id) {
        Manutencao m = manutencaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Manutencao não encontrada"));
        return toDto(m);
    }

    public List<ManutencaoDTO> listByClient(UUID clienteId) {
        return manutencaoRepository.findByClient_IdCliente(clienteId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(UUID id) {
        manutencaoRepository.deleteById(id);
    }

    private ManutencaoDTO toDto(Manutencao m) {
        ManutencaoDTO dto = new ManutencaoDTO();
        dto.setIdManutencao(m.getIdManutencao());
        dto.setIdCliente(m.getClient().getIdCliente());
        dto.setTipoManutencao(m.getTipoManutencao());
        dto.setDataManutencao(m.getDataManutencao());
        if (m.getResiduos() != null) {
            dto.setResiduos(m.getResiduos().stream().map(mr -> new ManutencaoResiduoDTO(mr.getId(), mr.getResiduo().getIdResiduo(), mr.getQuantidade())).collect(Collectors.toList()));
        }
        return dto;
    }
}
