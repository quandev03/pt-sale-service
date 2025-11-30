package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.ContractDTO;
import com.vnsky.bcss.projectbase.domain.entity.ContractEntity;
import com.vnsky.bcss.projectbase.domain.mapper.ContractMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.ContractRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.ContractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ContractAdapter extends BaseJPAAdapterVer2<
    ContractEntity,
    ContractDTO,
    String,
    ContractMapper,
    ContractRepository> implements ContractRepoPort {

    private final ContractRepository repository;
    private final ContractMapper mapper;

    public ContractAdapter(ContractRepository repository, ContractMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ContractDTO save(ContractDTO dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public Optional<ContractDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Page<ContractDTO> search(String ownerName, String tenantName, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        return repository.search(ownerName, tenantName, fromDate, toDate, pageable)
            .map(mapper::toDto);
    }
}

