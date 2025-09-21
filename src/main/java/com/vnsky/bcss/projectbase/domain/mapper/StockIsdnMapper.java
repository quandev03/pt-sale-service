package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockIsdnMapper extends BaseMapper<StockIsdnEntity, StockIsdnDTO> {

} 