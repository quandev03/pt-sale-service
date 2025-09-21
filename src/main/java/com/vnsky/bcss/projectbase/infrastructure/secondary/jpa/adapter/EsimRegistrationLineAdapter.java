package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationLineEntity;
import com.vnsky.bcss.projectbase.domain.mapper.EsimRegistrationLineMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.EsimRegistrationLineRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.EsimRegistrationLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EsimRegistrationLineAdapter extends BaseJPAAdapterVer2<EsimRegistrationLineEntity, EsimRegistrationLineDTO, String, EsimRegistrationLineMapper, EsimRegistrationLineRepository>
implements EsimRegistrationLineRepoPort {

    public EsimRegistrationLineAdapter(EsimRegistrationLineMapper mapper,
                                      EsimRegistrationLineRepository repository) {
        super(repository, mapper);
    }

    @Override
    public EsimRegistrationLineDTO saveAndFlush(EsimRegistrationLineDTO esimRegistrationLineDTO) {
        return super.saveAndFlush(esimRegistrationLineDTO);
    }

    @Override
    public List<EsimRegistrationLineDTO> saveAllAndFlush(List<EsimRegistrationLineDTO> esimRegistrationLineDTOs) {
        return super.saveAllAndFlush(esimRegistrationLineDTOs);
    }
} 