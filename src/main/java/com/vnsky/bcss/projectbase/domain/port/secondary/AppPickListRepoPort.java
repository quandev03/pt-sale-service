package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.entity.AppPickListEntity;

import java.util.List;

public interface AppPickListRepoPort {
    List<AppPickListEntity> findByTableNameAndColumnName(String tableName, String columnName);

    void patchToDataList(List<?> objects);

    List<AppPickListEntity> findAllActive();
}
