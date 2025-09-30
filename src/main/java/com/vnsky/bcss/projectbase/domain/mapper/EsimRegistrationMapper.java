package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationDTO;
import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EsimRegistrationMapper extends BaseMapper<EsimRegistrationEntity, EsimRegistrationDTO> {
}
