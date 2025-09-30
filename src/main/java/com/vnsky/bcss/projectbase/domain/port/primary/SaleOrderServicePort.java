package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrderRevenueReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public interface SaleOrderServicePort {
    Page<OrderRevenueReportResponse> searchOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);

    ByteArrayOutputStream exportOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request);

    List<StatisticResponse> statisticEsimSold(String orgCode, String startDate, String endDate, int granularity);

    List<StatisticOrgResponse> statisticEsimSoldOrg(String orgCode, String startDate, String endDate);

    BigDecimal revenue(String orgCode);
}
