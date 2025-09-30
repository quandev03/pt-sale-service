package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.ListBookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrderRevenueReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SaleOrderRepoPort {
    SaleOrderDTO saveAndFlush(SaleOrderDTO saleOrderDTO);
    Page<OrderRevenueReportResponse> searchOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);
    List<OrderRevenueReportResponse> getOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request);
    Page<SaleOrderDTO> getListBookEsimFreeByOrgId(Pageable pageable, String orgId, String toDate, String fromDate, String textSearch, int isFree);
    Page<ListBookEsimResponse> getListBookEsimByOrgId(Pageable pageable, String orgId, String toDate, String fromDate, String textSearch);
    List<ListBookEsimResponse> getListBookEsimByOrgIdExport(String orgId, String toDate, String fromDate, String textSearch);
    Optional<SaleOrderDTO> findById(String id);
    List<StatisticResponse> statisticEsimSold(String orgCode, String startDate, String endDate, int granularity);
    List<StatisticOrgResponse> statisticEsimSoldOrg(String orgCode, String startDate, String endDate);
    BigDecimal revenue(String orgCode);

    // Báo cáo tổng hợp theo từng tổ chức - với pagination
    Page<SummaryByOrgReportDTO> getSummaryByOrgReport(String startDate, String endDate, Pageable pageable);

    // Báo cáo tổng hợp theo từng tổ chức - tất cả dữ liệu cho export
    List<SummaryByOrgReportDTO> getSummaryByOrgReportAll(String startDate, String endDate);

    // Báo cáo tổng hợp toàn bộ
    SummaryAllReportDTO getSummaryAllReport(String startDate, String endDate);
}
