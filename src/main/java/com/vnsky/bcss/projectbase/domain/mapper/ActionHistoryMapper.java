package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.entity.ActionHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActionHistoryMapper extends BaseMapper<ActionHistoryEntity, ActionHistoryDTO> {
}
