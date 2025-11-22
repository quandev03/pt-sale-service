package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.domain.entity.RoomServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomServiceMapper extends BaseMapper<RoomServiceEntity, RoomServiceDTO> {
}

