package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDTO;
import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomPaymentMapper extends BaseMapper<RoomPaymentEntity, RoomPaymentDTO> {
}

