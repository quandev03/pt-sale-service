package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho báo cáo tổng hợp toàn bộ
 * Format: Tổng hợp kết quả (Kênh Đại lý)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryAllReportDTO {
    // Nguồn: Mặc định "Reseller"
    private String source;

    // Số lượng Đại lý phát sinh giao dịch
    @DbColumnMapper("total_agents")
    private Long totalAgents; // Tổng đại lý
    @DbColumnMapper("total_agents_today")
    private Long totalAgentsToday; // Ngày dd/mm/yyyy (toDate)
    @DbColumnMapper("total_agents_month")
    private Long totalAgentsMonth; // LK tháng mm/yyyy
    @DbColumnMapper("total_agents_year")
    private Long totalAgentsYear; // LK năm yyyy

    // Số lượng Thuê Bao Đấu Nối
    @DbColumnMapper("total_esim_connected")
    private Long totalEsimConnected; // Tổng số eSIM đặt
    @DbColumnMapper("total_esim_connected_today")
    private Long totalEsimConnectedToday; // Ngày (toDate)
    @DbColumnMapper("total_esim_connected_month")
    private Long totalEsimConnectedMonth; // LK tháng mm/yyyy
    @DbColumnMapper("total_esim_connected_month_growth")
    private Double totalEsimConnectedMonthGrowth; // So với tháng trước (%)
    @DbColumnMapper("total_esim_connected_year")
    private Long totalEsimConnectedYear; // LK năm yyyy

    // Số lượng Thuê Bao Mua Gói
    @DbColumnMapper("total_esim_purchased")
    private Long totalEsimPurchased; // Tổng số eSIM đã gọi 900 và gán gói
    @DbColumnMapper("total_esim_purchased_today")
    private Long totalEsimPurchasedToday; // Ngày (toDate)
    @DbColumnMapper("total_esim_purchased_month")
    private Long totalEsimPurchasedMonth; // LK tháng mm/yyyy
    @DbColumnMapper("total_esim_purchased_month_growth")
    private Double totalEsimPurchasedMonthGrowth; // So với tháng trước (%)
    @DbColumnMapper("total_esim_purchased_year")
    private Long totalEsimPurchasedYear; // LK năm yyyy

    // Thông tin ngày báo cáo
    private String reportDate; // dd/mm/yyyy
    private String reportMonth; // mm/yyyy
    private String reportYear; // yyyy
}
