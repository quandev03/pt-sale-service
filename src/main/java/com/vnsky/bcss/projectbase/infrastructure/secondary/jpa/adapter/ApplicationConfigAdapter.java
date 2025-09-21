package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.domain.entity.ApplicationConfigEntity;
import com.vnsky.bcss.projectbase.domain.mapper.ApplicationConfigMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.ApplicationConfigRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.ApplicationConfigRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class ApplicationConfigAdapter extends BaseJPAAdapterVer2<ApplicationConfigEntity, ApplicationConfigDTO, String, ApplicationConfigMapper, ApplicationConfigRepository>
implements ApplicationConfigRepoPort {

    public ApplicationConfigAdapter(ApplicationConfigRepository repository, ApplicationConfigMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public List<ApplicationConfigDTO> getByTypeAndLanguage(String type, String language) {
        return mapper.toListDto(repository.getByTypeAndLanguage(type, language));
    }

    @Override
    public ApplicationConfigDTO getByTableNameAndColumnName(String tableName, String columnName) {
        return repository.findByTypeAndCode(tableName, columnName).map(mapper::toDto)
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.APPLICATION_CONFIG_MISSING).addParameter("type", tableName)
            .addParameter("code", columnName).build());
    }
}
