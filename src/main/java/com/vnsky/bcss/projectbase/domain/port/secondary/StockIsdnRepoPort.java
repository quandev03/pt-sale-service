package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchStockIsdnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StockIsdnRepoPort {
    List<StockIsdnDTO> findAvailableIsdns(int limit);

    Optional<StockIsdnDTO> findByIsdn(Long isdn);

    Optional<StockIsdnDTO> findBySerial(Long serial);

    List<StockIsdnDTO> saveAllAndFlush(List<StockIsdnDTO> stockIsdnDTOs);

    void updateTransferStatusIsdnIn(Integer transferStatus, Integer transferType, List<Long> isdnList);

    void createStockIsdnIn(List<Long> isdnList);

    List<StockIsdnDTO> findByIsdnIn(List<Long> isdns);

    List<StockIsdnDTO> findByIsdnIn(Set<Long> isdns);

    Page<SearchStockIsdnResponse> search(String q, Integer status, String orgCode, Pageable page);

    List<SearchStockIsdnResponse> getSubscriber(String q, Integer status, String orgCode);

    Long totalEsim(Integer status, String orgCode);

    Long revenusEsimCalled900(Integer status, String orgCode);
}
