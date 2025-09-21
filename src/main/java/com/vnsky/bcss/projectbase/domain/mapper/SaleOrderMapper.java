package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleOrderMapper extends BaseMapper<SaleOrderEntity, SaleOrderDTO> {

}
