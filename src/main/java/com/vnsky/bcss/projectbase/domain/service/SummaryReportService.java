package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportResponseDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.SummaryReportServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderRepoPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.CustomExcelWriter;
import com.vnsky.bcss.projectbase.shared.utils.CustomExcelResource;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.List;

/**
 * Service xử lý báo cáo tổng hợp
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryReportService implements SummaryReportServicePort {

    private final SaleOrderRepoPort saleOrderRepoPort;

    @Override
    public SummaryByOrgReportResponseDTO searchSummaryByOrgReport(String startDate, String endDate, Pageable pageable) {
        log.info("Tìm kiếm báo cáo tổng hợp kết quả thuê bao - gói cước - startDate: {}, endDate: {}", startDate, endDate);
        
        // Lấy dữ liệu phân trang
        Page<SummaryByOrgReportDTO> dataPage = saleOrderRepoPort.getSummaryByOrgReport(startDate, endDate, pageable);
        
        // Tính tổng cho tất cả dữ liệu (không chỉ page hiện tại)
        List<SummaryByOrgReportDTO> allData = saleOrderRepoPort.getSummaryByOrgReportAll(startDate, endDate);
        SummaryByOrgReportResponseDTO.SummaryByOrgReportTotalsDTO totals = calculateTotals(allData);
        
        return SummaryByOrgReportResponseDTO.builder()
                .data(dataPage)
                .totals(totals)
                .build();
    }

    @Override
    public SummaryAllReportDTO searchSummaryAllReport(String startDate, String endDate) {
        log.info("Tìm kiếm báo cáo tổng hợp toàn bộ - startDate: {}, endDate: {}", startDate, endDate);
        return saleOrderRepoPort.getSummaryAllReport(startDate, endDate);
    }

    @Override
    public Resource exportSummaryByOrgReport(String startDate, String endDate) {
        log.info("Bắt đầu xuất Excel cho Báo cáo tổng hợp kết quả thuê bao - gói cước");

        try {
            List<SummaryByOrgReportDTO> data = saleOrderRepoPort.getSummaryByOrgReportAll(startDate, endDate);

            byte[] excelBytes = CustomExcelWriter.createSummaryByOrgExcel(data, startDate, endDate);
            String datePart = buildDatePart(startDate, endDate);
            String filename = String.format("Bao_cao_ket_qua_thue_bao_goi_cuoc_%s.xlsx", datePart);

            return new CustomExcelResource(excelBytes, filename);
        } catch (Exception e) {
            log.error("Lỗi khi xuất Excel: {}", e.getMessage(), e);
            throw BaseException.bussinessError(ErrorCode.ERROR_EXPORT_EXCEL).build();
        }
    }

    @Override
    public Resource exportSummaryAllReport(String startDate, String endDate) {
        log.info("Bắt đầu xuất Excel cho Báo cáo tổng hợp kết quả");

        try {
            SummaryAllReportDTO data = saleOrderRepoPort.getSummaryAllReport(startDate, endDate);

            byte[] excelBytes = CustomExcelWriter.createSummaryAllExcel(data, startDate, endDate);
            String datePart = buildDatePart(startDate, endDate);
            String filename = String.format("Bao_cao_tong_hop_ket_qua_%s.xlsx", datePart);

            return new CustomExcelResource(excelBytes, filename);
        } catch (Exception e) {
            log.error("Lỗi khi xuất Excel: {}", e.getMessage(), e);
            throw BaseException.bussinessError(ErrorCode.ERROR_EXPORT_EXCEL).build();
        }
    }


    /**
     * Tính tổng cho tất cả các field số trong danh sách SummaryByOrgReportDTO
     */
    private SummaryByOrgReportResponseDTO.SummaryByOrgReportTotalsDTO calculateTotals(List<SummaryByOrgReportDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return SummaryByOrgReportResponseDTO.SummaryByOrgReportTotalsDTO.builder()
                    .totalEsimOrdered(0L)
                    .totalEsimOrderedToday(0L)
                    .totalEsimOrderedMonth(0L)
                    .totalEsimOrderedYear(0L)
                    .totalEsimActivated900(0L)
                    .totalEsimActivated900Today(0L)
                    .totalEsimActivated900Month(0L)
                    .totalEsimActivated900Year(0L)
                    .totalRevenue(BigDecimal.ZERO)
                    .totalRevenueToday(BigDecimal.ZERO)
                    .totalRevenueMonth(BigDecimal.ZERO)
                    .totalRevenueYear(BigDecimal.ZERO)
                    .build();
        }

        // Tính tổng cho các field Long
        Long totalEsimOrdered = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimOrdered(), 0L))
                .sum();
        
        Long totalEsimOrderedToday = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimOrderedToday(), 0L))
                .sum();
        
        Long totalEsimOrderedMonth = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimOrderedMonth(), 0L))
                .sum();
        
        Long totalEsimOrderedYear = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimOrderedYear(), 0L))
                .sum();
        
        Long totalEsimActivated900 = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimActivated900(), 0L))
                .sum();
        
        Long totalEsimActivated900Today = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimActivated900Today(), 0L))
                .sum();
        
        Long totalEsimActivated900Month = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimActivated900Month(), 0L))
                .sum();
        
        Long totalEsimActivated900Year = dataList.stream()
                .mapToLong(item -> Objects.requireNonNullElse(item.getTotalEsimActivated900Year(), 0L))
                .sum();

        // Tính tổng cho các field BigDecimal
        BigDecimal totalRevenue = dataList.stream()
                .map(item -> Objects.requireNonNullElse(item.getTotalRevenue(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRevenueToday = dataList.stream()
                .map(item -> Objects.requireNonNullElse(item.getTotalRevenueToday(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRevenueMonth = dataList.stream()
                .map(item -> Objects.requireNonNullElse(item.getTotalRevenueMonth(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRevenueYear = dataList.stream()
                .map(item -> Objects.requireNonNullElse(item.getTotalRevenueYear(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SummaryByOrgReportResponseDTO.SummaryByOrgReportTotalsDTO.builder()
                .totalEsimOrdered(totalEsimOrdered)
                .totalEsimOrderedToday(totalEsimOrderedToday)
                .totalEsimOrderedMonth(totalEsimOrderedMonth)
                .totalEsimOrderedYear(totalEsimOrderedYear)
                .totalEsimActivated900(totalEsimActivated900)
                .totalEsimActivated900Today(totalEsimActivated900Today)
                .totalEsimActivated900Month(totalEsimActivated900Month)
                .totalEsimActivated900Year(totalEsimActivated900Year)
                .totalRevenue(totalRevenue)
                .totalRevenueToday(totalRevenueToday)
                .totalRevenueMonth(totalRevenueMonth)
                .totalRevenueYear(totalRevenueYear)
                .build();
    }

    private String buildDatePart(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        if (start.isEqual(end)) {
            return startDate;
        }
        return startDate + "_" + endDate;
    }
}
