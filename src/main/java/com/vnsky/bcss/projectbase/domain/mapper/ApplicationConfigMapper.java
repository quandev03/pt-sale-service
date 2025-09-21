package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.domain.entity.ApplicationConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationConfigMapper extends BaseMapper<ApplicationConfigEntity, ApplicationConfigDTO>{

}
