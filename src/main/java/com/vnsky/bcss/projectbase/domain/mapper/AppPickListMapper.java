package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.AppPickListDTO;
import com.vnsky.bcss.projectbase.domain.entity.AppPickListEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppPickListMapper extends BaseMapper<AppPickListEntity, AppPickListDTO> {
}
