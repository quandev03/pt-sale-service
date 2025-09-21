package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SaleOrderRepoPort {
    SaleOrderDTO saveAndFlush(SaleOrderDTO saleOrderDTO);
    Page<SaleOrderDTO> getListBookEsimFreeByOrgId(Pageable pageable, String orgId);

}
