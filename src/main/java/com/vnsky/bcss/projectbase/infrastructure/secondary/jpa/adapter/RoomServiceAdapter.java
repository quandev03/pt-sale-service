package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.domain.entity.RoomServiceEntity;
import com.vnsky.bcss.projectbase.domain.mapper.RoomServiceMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomServiceRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.RoomServiceRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoomServiceAdapter extends BaseJPAAdapterVer2<
    RoomServiceEntity,
    RoomServiceDTO,
    String,
    RoomServiceMapper,
    RoomServiceRepository> implements RoomServiceRepoPort {

    private final RoomServiceRepository repository;
    private final RoomServiceMapper mapper;

    public RoomServiceAdapter(RoomServiceRepository repository, RoomServiceMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RoomServiceDTO save(RoomServiceDTO dto) {
        return super.save(dto);
    }

    @Override
    public RoomServiceDTO update(RoomServiceDTO dto) {
        return super.update(dto);
    }

    @Override
    public Optional<RoomServiceDTO> findById(String id) {
        return repository.findById(id)
            .map(mapper::toDto);
    }

    @Override
    public Optional<RoomServiceDTO> findByClientIdAndServiceCode(String clientId, String serviceCode) {
        return repository.findByClientIdAndServiceCode(clientId, serviceCode)
            .map(mapper::toDto);
    }

    @Override
    public Optional<RoomServiceDTO> findByClientIdAndServiceCodeExcludingId(String clientId, String serviceCode, String id) {
        return repository.findByClientIdAndServiceCodeAndIdNot(clientId, serviceCode, id)
            .map(mapper::toDto);
    }

    @Override
    public List<RoomServiceDTO> findByOrgUnitId(String orgUnitId) {
        return repository.findByOrgUnitIdOrderByCreatedDateDesc(orgUnitId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public List<RoomServiceDTO> findByClientId(String clientId) {
        return repository.findByClientIdOrderByCreatedDateDesc(clientId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public List<RoomServiceDTO> findByFilters(String clientId, String orgUnitId, RoomServiceType serviceType, Integer status) {
        return repository.findByFilters(clientId, orgUnitId, serviceType, status)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

