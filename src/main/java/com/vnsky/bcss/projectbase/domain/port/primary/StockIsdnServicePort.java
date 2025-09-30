package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchStockIsdnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;

public interface StockIsdnServicePort {

    Page<SearchStockIsdnResponse> search(String q, Integer status, String orgCode, Pageable page);

    ByteArrayOutputStream exportSubscriber(String q, Integer status, String orgCode);

    Long totalEsimProcured(String orgCode);

    Long totalEsimSold(String orgCode);

    Long totalSTBCalled900(String orgCode);

    Long revenusSTBCalled900(String orgCode);
}
