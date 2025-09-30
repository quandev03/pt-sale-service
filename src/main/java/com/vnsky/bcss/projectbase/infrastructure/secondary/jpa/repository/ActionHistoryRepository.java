package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.ActionHistoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionHistoryRepository extends BaseJPARepository<ActionHistoryEntity, String> {
    @Query(value = """
        SELECT * FROM ACTION_HISTORY ah
        WHERE ah.SUB_ID = :subId
        AND ah.REASON_CODE = 'VIEW'
        """,  nativeQuery = true)
    List<ActionHistoryEntity> findAllBySubId(@Param("subId") String subId, Sort sort);

    ActionHistoryEntity findByActionCodeAndSubId(String actionCode, String subId);
}
