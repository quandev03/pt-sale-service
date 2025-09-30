package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.BatchPackageSaleDTO;
import com.vnsky.bcss.projectbase.domain.entity.BatchPackageSaleEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BatchPackageSaleMapper extends BaseMapper<BatchPackageSaleEntity, BatchPackageSaleDTO> {

}
