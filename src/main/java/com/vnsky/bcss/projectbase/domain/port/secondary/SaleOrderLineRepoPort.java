package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderLineDTO;

import java.util.List;

public interface SaleOrderLineRepoPort {
    SaleOrderLineDTO saveAndFlush(SaleOrderLineDTO saleOrderLineDTO);
    List<SaleOrderLineDTO> saveAllAndFlush(List<SaleOrderLineDTO> saleOrderLineDTOs);
} 