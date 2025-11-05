package br.com.ecogov.compliance.services;

import br.com.ecogov.compliance.dtos.ResiduoDTO;
import br.com.ecogov.compliance.models.Residuo;
import br.com.ecogov.compliance.repositories.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResiduoService {

    private final ResiduoRepository residuoRepository;

    public ResiduoDTO create(ResiduoDTO dto) {
        Residuo r = new Residuo();
        r.setNomeResiduo(dto.getNomeResiduo());
        r.setUnidadeMedida(dto.getUnidadeMedida());
        r.setQuantidadeAcumulada(dto.getQuantidadeAcumulada());
        r.setLimiteReciclagem(dto.getLimiteReciclagem());
        Residuo saved = residuoRepository.save(r);
        dto.setIdResiduo(saved.getIdResiduo());
        return dto;
    }

    public ResiduoDTO findById(UUID id) {
        Residuo r = residuoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Residuo não encontrado"));
        return toDto(r);
    }

    public List<ResiduoDTO> listAll() {
        return residuoRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ResiduoDTO update(UUID id, ResiduoDTO dto) {
        Residuo r = residuoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Residuo não encontrado"));
        if (dto.getNomeResiduo() != null) r.setNomeResiduo(dto.getNomeResiduo());
        if (dto.getUnidadeMedida() != null) r.setUnidadeMedida(dto.getUnidadeMedida());
        if (dto.getQuantidadeAcumulada() != null) r.setQuantidadeAcumulada(dto.getQuantidadeAcumulada());
        if (dto.getLimiteReciclagem() != null) r.setLimiteReciclagem(dto.getLimiteReciclagem());
        Residuo saved = residuoRepository.save(r);
        return toDto(saved);
    }

    public void delete(UUID id) {
        residuoRepository.deleteById(id);
    }

    private ResiduoDTO toDto(Residuo r) {
        ResiduoDTO dto = new ResiduoDTO();
        dto.setIdResiduo(r.getIdResiduo());
        dto.setNomeResiduo(r.getNomeResiduo());
        dto.setUnidadeMedida(r.getUnidadeMedida());
        dto.setQuantidadeAcumulada(r.getQuantidadeAcumulada());
        dto.setLimiteReciclagem(r.getLimiteReciclagem());
        return dto;
    }
}
