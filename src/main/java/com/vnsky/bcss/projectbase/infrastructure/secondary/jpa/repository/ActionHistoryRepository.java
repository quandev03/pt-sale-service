package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.entity.ActionHistoryEntity;
import com.vnsky.bcss.projectbase.domain.mapper.BaseMapper;

import java.util.List;

public interface ActionHistoryRepository extends BaseJPARepository<ActionHistoryEntity, String> {
    List<ActionHistoryEntity> findAllBySubId(String subId);

    ActionHistoryEntity findByActionCodeAndSubId(String actionCode, String subId);
}
