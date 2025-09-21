package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.SaleOrderLineEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleOrderLineMapper extends BaseMapper<SaleOrderLineEntity, SaleOrderLineDTO> {

} 