package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationDeliveryInfoDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationDeliveryInfoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationDeliveryInfosMapper extends BaseMapper<OrganizationDeliveryInfoEntity, OrganizationDeliveryInfoDTO> {
}
