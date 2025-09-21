package com.vnsky.bcss.projectbase.domain.mapper;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationUnitMapper extends BaseMapper<OrganizationUnitEntity, OrganizationUnitDTO> {

}
