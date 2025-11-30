package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ContractDTO;
import com.vnsky.bcss.projectbase.domain.entity.ContractEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContractMapper extends BaseMapper<ContractEntity, ContractDTO> {
}

