package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitImageDTO;

import java.util.List;

public interface OrganizationUnitImageRepoPort {

    OrganizationUnitImageDTO save(OrganizationUnitImageDTO dto);

    List<OrganizationUnitImageDTO> saveAll(List<OrganizationUnitImageDTO> dtos);

    List<OrganizationUnitImageDTO> findByOrgUnitId(String orgUnitId);

    void deleteByOrgUnitId(String orgUnitId);

    void delete(OrganizationUnitImageDTO dto);
}





