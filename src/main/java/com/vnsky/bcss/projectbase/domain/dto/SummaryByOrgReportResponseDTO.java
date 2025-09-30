package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * Response DTO cho báo cáo tổng hợp theo từng tổ chức
 * Bao gồm dữ liệu phân trang và tổng cho mỗi field
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryByOrgReportResponseDTO {

    /**
     * Dữ liệu phân trang
     */
    private Page<SummaryByOrgReportDTO> data;

    /**
     * Tổng cho tất cả các field số
     */
    private SummaryByOrgReportTotalsDTO totals;

    /**
     * DTO chứa tổng cho tất cả các field số của SummaryByOrgReportDTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryByOrgReportTotalsDTO {

        // Tổng số lượng thuê bao đặt hàng
        private Long totalEsimOrdered;
        private Long totalEsimOrderedToday;
        private Long totalEsimOrderedMonth;
        private Long totalEsimOrderedYear;

        // Tổng số lượng Thuê Bao kích hoạt 900
        private Long totalEsimActivated900;
        private Long totalEsimActivated900Today;
        private Long totalEsimActivated900Month;
        private Long totalEsimActivated900Year;

        // Tổng doanh thu gói cước
        private BigDecimal totalRevenue;
        private BigDecimal totalRevenueToday;
        private BigDecimal totalRevenueMonth;
        private BigDecimal totalRevenueYear;
    }
}
