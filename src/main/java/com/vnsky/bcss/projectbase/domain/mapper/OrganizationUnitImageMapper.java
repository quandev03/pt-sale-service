package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitImageDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationUnitImageMapper extends BaseMapper<OrganizationUnitImageEntity, OrganizationUnitImageDTO> {
}

