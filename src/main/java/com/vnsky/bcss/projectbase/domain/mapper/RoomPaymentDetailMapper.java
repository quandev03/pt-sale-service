package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDetailDTO;
import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentDetailEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomPaymentDetailMapper extends BaseMapper<RoomPaymentDetailEntity, RoomPaymentDetailDTO> {
}

