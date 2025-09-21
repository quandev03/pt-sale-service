package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;

import java.util.List;
import java.util.Optional;

public interface StockIsdnRepoPort {
    List<StockIsdnDTO> findAvailableIsdns(int limit);

    Optional<StockIsdnDTO> findByIsdn(Long isdn);

    Optional<StockIsdnDTO> findBySerial(Long serial);

    List<StockIsdnDTO> saveAllAndFlush(List<StockIsdnDTO> stockIsdnDTOs);
}
