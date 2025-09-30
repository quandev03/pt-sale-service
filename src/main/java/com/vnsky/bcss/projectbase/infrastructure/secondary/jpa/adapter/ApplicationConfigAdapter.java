package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.domain.dto.CountryCodeDTO;
import com.vnsky.bcss.projectbase.domain.entity.ApplicationConfigEntity;
import com.vnsky.bcss.projectbase.domain.mapper.ApplicationConfigMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.ApplicationConfigRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.ApplicationConfigRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.database.service.DatabaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class ApplicationConfigAdapter extends BaseJPAAdapterVer2<ApplicationConfigEntity, ApplicationConfigDTO, String, ApplicationConfigMapper, ApplicationConfigRepository>
implements ApplicationConfigRepoPort {
    private final DatabaseMapper dbmapper;

    public ApplicationConfigAdapter(ApplicationConfigRepository repository, ApplicationConfigMapper mapper, DatabaseMapper dbmapper) {
        super(repository, mapper);
        this.dbmapper = dbmapper;
    }

    @Override
    public List<ApplicationConfigDTO> getByTypeAndLanguage(String type, String language) {
        return mapper.toListDto(repository.getByTypeAndLanguage(type, language));
    }

    @Override
    public List<ApplicationConfigDTO> getByType(String type) {
        return mapper.toListDto(repository.getByType(type));
    }

    @Override
    public ApplicationConfigDTO getByTableNameAndColumnName(String tableName, String columnName) {
        return repository.findByTypeAndCode(tableName, columnName).map(mapper::toDto)
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.APPLICATION_CONFIG_MISSING).addParameter("type", tableName)
            .addParameter("code", columnName).build());
    }

    @Override
    public List<CountryCodeDTO> getCountryCodes(String type) {
        return dbmapper.castSqlResult(repository.getCountryCodes(type), CountryCodeDTO.class);
    }
}
