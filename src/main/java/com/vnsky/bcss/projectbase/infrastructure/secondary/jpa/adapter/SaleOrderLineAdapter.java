package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.SaleOrderLineEntity;
import com.vnsky.bcss.projectbase.domain.mapper.SaleOrderLineMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderLineRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SaleOrderLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SaleOrderLineAdapter extends BaseJPAAdapterVer2<SaleOrderLineEntity, SaleOrderLineDTO, String, SaleOrderLineMapper, SaleOrderLineRepository>
implements SaleOrderLineRepoPort {

    public SaleOrderLineAdapter(SaleOrderLineMapper mapper,
                               SaleOrderLineRepository repository) {
        super(repository, mapper);
    }

    @Override
    public List<SaleOrderLineDTO> findBySaleOrderId(String saleOrderId) {
        List<SaleOrderLineEntity> entities = repository.findBySaleOrderId(saleOrderId);
        return entities.stream()
            .map(mapper::toDto)
            .toList();
    }
}
