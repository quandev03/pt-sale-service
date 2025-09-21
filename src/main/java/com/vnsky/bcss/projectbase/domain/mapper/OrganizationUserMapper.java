package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationUserMapper extends BaseMapper<OrganizationUserEntity, OrganizationUserDTO> {

}
