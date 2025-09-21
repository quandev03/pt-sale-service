package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import com.vnsky.bcss.projectbase.domain.mapper.SaleOrderMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SaleOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SaleOrderAdapter extends BaseJPAAdapterVer2<SaleOrderEntity, SaleOrderDTO, String, SaleOrderMapper, SaleOrderRepository>
implements SaleOrderRepoPort {

    public SaleOrderAdapter(SaleOrderMapper mapper,
                           SaleOrderRepository repository) {
        super(repository, mapper);
    }

    @Override
    public SaleOrderDTO saveAndFlush(SaleOrderDTO saleOrderDTO) {
        return super.saveAndFlush(saleOrderDTO);
    }
    public Page<SaleOrderDTO> getListBookEsimFreeByOrgId(Pageable pageable, String orgId) {
        return repository.findBookFreeByOrgId(pageable, orgId).map(entity -> mapper.toDto(entity));
    }
}
