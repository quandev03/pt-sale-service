package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import com.vnsky.bcss.projectbase.domain.entity.AgentDebitEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentDebitMapper extends BaseMapper<AgentDebitEntity, AgentDebitDTO> {
}
