package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.AppPickListEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppPickListRepository extends BaseJPARepository<AppPickListEntity, String> {

    List<AppPickListEntity> findByTableNameAndColumnName(String tableName, String columnName);

    @Query(value = "select a from AppPickListEntity a where a.status = 1")
    List<AppPickListEntity> findAllActive();
}
