package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho báo cáo tổng hợp theo từng tổ chức
 * Format: Kết quả thuê bao - gói cước (Kênh Reseller)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryByOrgReportDTO {
    // Công ty khu vực (root org - most parent hoặc chính nó nếu không có parent)
    @DbColumnMapper("ROOT_ORG_CODE")
    private String rootOrgCode;
    @DbColumnMapper("ROOT_ORG_NAME")
    private String rootOrgName;

    // Tên Đại lý
    @DbColumnMapper("ORG_CODE")
    private String orgCode;
    @DbColumnMapper("ORG_NAME")
    private String orgName;

    // Số lượng thuê bao đặt hàng
    @DbColumnMapper("total_esim_ordered")
    private Long totalEsimOrdered; // Tổng số eSIM đặt
    @DbColumnMapper("total_esim_ordered_today")
    private Long totalEsimOrderedToday; // Ngày (toDate)
    @DbColumnMapper("total_esim_ordered_month")
    private Long totalEsimOrderedMonth; // LK tháng mm/yyyy
    @DbColumnMapper("total_esim_ordered_month_growth")
    private Double totalEsimOrderedMonthGrowth; // So với tháng trước (%)
    @DbColumnMapper("total_esim_ordered_year")
    private Long totalEsimOrderedYear; // LK năm yyyy

    // Số lượng Thuê Bao kích hoạt 900
    @DbColumnMapper("total_esim_activated_900")
    private Long totalEsimActivated900; // Tổng số eSIM đã gọi 900
    @DbColumnMapper("total_esim_activated_900_today")
    private Long totalEsimActivated900Today; // Ngày (toDate)
    @DbColumnMapper("total_esim_activated_900_month")
    private Long totalEsimActivated900Month; // LK tháng mm/yyyy
    @DbColumnMapper("total_esim_activated_900_month_growth")
    private Double totalEsimActivated900MonthGrowth; // So với tháng trước (%)
    @DbColumnMapper("total_esim_activated_900_year")
    private Long totalEsimActivated900Year; // LK năm yyyy

    // Doanh thu gói cước
    @DbColumnMapper("total_revenue")
    private BigDecimal totalRevenue; // Tổng doanh thu
    @DbColumnMapper("total_revenue_today")
    private BigDecimal totalRevenueToday; // Ngày (toDate)
    @DbColumnMapper("total_revenue_month")
    private BigDecimal totalRevenueMonth; // LK tháng mm/yyyy
    @DbColumnMapper("total_revenue_month_growth")
    private Double totalRevenueMonthGrowth; // So với tháng trước (%)
    @DbColumnMapper("total_revenue_year")
    private BigDecimal totalRevenueYear; // LK năm yyyy

    // Thông tin ngày báo cáo
    private String reportDate; // dd/mm/yyyy
    private String reportMonth; // mm/yyyy
    private String reportYear; // yyyy
}
