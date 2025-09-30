package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationDTO;
import com.vnsky.bcss.projectbase.domain.dto.IncompleteRegistrationRowDTO;
import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationEntity;
import com.vnsky.bcss.projectbase.domain.mapper.EsimRegistrationMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.EsimRegistrationRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.EsimRegistrationRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class EsimRegistrationAdapter extends BaseJPAAdapterVer2<EsimRegistrationEntity, EsimRegistrationDTO, String, EsimRegistrationMapper, EsimRegistrationRepository>
implements EsimRegistrationRepoPort {

    private final DbMapper dbMapper;

    public EsimRegistrationAdapter(DbMapper dbMapper, EsimRegistrationRepository repository, EsimRegistrationMapper mapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Optional<EsimRegistrationDTO> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public EsimRegistrationDTO saveAndFlush(EsimRegistrationDTO dto) {
        EsimRegistrationEntity entity = mapper.toEntity(dto);
        entity.setModifiedBy(dto.getModifiedBy());
        return mapper.toDto(repository.saveAndFlush(entity));
    }

    @Override
    public List<IncompleteRegistrationRowDTO> findIncompleteRegistrations() {
        List<Tuple> results = repository.findIncompleteRegistrations();
        return dbMapper.castSqlResult(results, IncompleteRegistrationRowDTO.class);
    }
}
