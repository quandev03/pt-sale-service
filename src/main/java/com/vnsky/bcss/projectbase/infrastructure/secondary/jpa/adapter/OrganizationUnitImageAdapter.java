package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitImageDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitImageEntity;
import com.vnsky.bcss.projectbase.domain.mapper.OrganizationUnitImageMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitImageRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUnitImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganizationUnitImageAdapter extends BaseJPAAdapterVer2<
    OrganizationUnitImageEntity,
    OrganizationUnitImageDTO,
    String,
    OrganizationUnitImageMapper,
    OrganizationUnitImageRepository> implements OrganizationUnitImageRepoPort {

    private final OrganizationUnitImageRepository repository;
    private final OrganizationUnitImageMapper mapper;

    public OrganizationUnitImageAdapter(OrganizationUnitImageRepository repository,
                                       OrganizationUnitImageMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OrganizationUnitImageDTO save(OrganizationUnitImageDTO dto) {
        return super.save(dto);
    }

    @Override
    public List<OrganizationUnitImageDTO> saveAll(List<OrganizationUnitImageDTO> dtos) {
        return super.saveAll(dtos);
    }

    @Override
    public List<OrganizationUnitImageDTO> findByOrgUnitId(String orgUnitId) {
        return repository.findByOrgUnitIdOrderBySortOrderAsc(orgUnitId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        repository.deleteByOrgUnitId(orgUnitId);
    }

    @Override
    public void delete(OrganizationUnitImageDTO dto) {
        repository.deleteById(dto.getId());
    }
}


