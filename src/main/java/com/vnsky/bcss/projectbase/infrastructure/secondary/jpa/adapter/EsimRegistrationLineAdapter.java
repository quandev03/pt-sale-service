package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.dto.BookEsimDetailLineItemDTO;
import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationLineEntity;
import com.vnsky.bcss.projectbase.domain.mapper.EsimRegistrationLineMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.EsimRegistrationLineRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.EsimRegistrationLineRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class EsimRegistrationLineAdapter extends BaseJPAAdapterVer2<EsimRegistrationLineEntity, EsimRegistrationLineDTO, String, EsimRegistrationLineMapper, EsimRegistrationLineRepository>
implements EsimRegistrationLineRepoPort {

    private final DbMapper dbMapper;

    public EsimRegistrationLineAdapter(EsimRegistrationLineMapper mapper,
                                      EsimRegistrationLineRepository repository,
                                      DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Optional<EsimRegistrationLineDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<BookEsimDetailLineItemDTO> findBookEsimDetailLineItemsBySaleOrderId(String saleOrderId) {
        List<Tuple> results = repository.findBookEsimDetailLineItemsBySaleOrderId(saleOrderId);
        return dbMapper.castSqlResult(results, BookEsimDetailLineItemDTO.class);
    }

    @Override
    public List<EsimRegistrationLineDTO> findIncompleteRegistrationLines() {
        List<Tuple> results = repository.findIncompleteRegistrationLines();
        return dbMapper.castSqlResult(results, EsimRegistrationLineDTO.class);
    }

    @Override
    public List<EsimRegistrationLineDTO> findByEsimRegistrationIdAndSerialIsNull(String esimRegistrationId) {
        List<Tuple> results = repository.findByEsimRegistrationIdAndSerialIsNull(esimRegistrationId);
        return dbMapper.castSqlResult(results, EsimRegistrationLineDTO.class);
    }
}
