package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;
import com.vnsky.bcss.projectbase.domain.mapper.AdvertisementMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.AdvertisementRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.AdvertisementRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdvertisementAdapter implements AdvertisementRepoPort {

    private final AdvertisementRepository repository;
    private final AdvertisementMapper mapper;

    @Override
    public AdvertisementDTO save(AdvertisementDTO dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public Optional<AdvertisementDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<AdvertisementDTO> findByIdAndClientId(String id, String clientId) {
        return repository.findByIdAndClientId(id, clientId).map(mapper::toDto);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<AdvertisementDTO> findByClientId(String clientId) {
        return repository.findByClientId(clientId).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<AdvertisementDTO> findActiveAdvertisements(LocalDateTime currentDate) {
        return repository.findActiveAdvertisements(AdvertisementStatus.ACTIVE, currentDate).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<AdvertisementDTO> findRandomActiveAdvertisement(LocalDateTime currentDate) {
        return repository.findRandomActiveAdvertisement(AdvertisementStatus.ACTIVE.name(), currentDate)
            .map(mapper::toDto);
    }

    @Override
    public void incrementViewCount(String id) {
        repository.findById(id).ifPresent(ad -> {
            ad.setViewCount(ad.getViewCount() != null ? ad.getViewCount() + 1 : 1L);
            repository.save(ad);
        });
    }
}


