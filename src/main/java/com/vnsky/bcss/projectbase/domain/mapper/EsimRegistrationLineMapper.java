package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationLineEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EsimRegistrationLineMapper extends BaseMapper<EsimRegistrationLineEntity, EsimRegistrationLineDTO> {

} 