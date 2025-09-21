package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;

import java.util.List;

public interface ApplicationConfigRepoPort {
    List<ApplicationConfigDTO> getByTypeAndLanguage(String type, String language);

    ApplicationConfigDTO getByTableNameAndColumnName(String tableName, String columnName);
}
