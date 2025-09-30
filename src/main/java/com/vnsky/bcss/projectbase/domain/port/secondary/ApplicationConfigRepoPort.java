package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.domain.dto.CountryCodeDTO;

import java.util.List;

public interface ApplicationConfigRepoPort {
    List<ApplicationConfigDTO> getByTypeAndLanguage(String type, String language);

    List<ApplicationConfigDTO> getByType(String type);

    ApplicationConfigDTO getByTableNameAndColumnName(String tableName, String columnName);

    List<CountryCodeDTO> getCountryCodes(String type);
}
