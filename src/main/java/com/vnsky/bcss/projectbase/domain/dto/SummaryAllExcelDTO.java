package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Summary All Excel export
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryAllExcelDTO {

    @XlsxColumn(index = 0, header = "STT")
    private Integer stt;

    @XlsxColumn(index = 1, header = "Nguồn")
    private String source;

    @XlsxColumn(index = 2, header = "Tổng đại lý")
    private Long totalAgents;

    @XlsxColumn(index = 3, header = "Số lượng Đại lý phát sinh giao dịch - Ngày")
    private Long totalAgentsToday;

    @XlsxColumn(index = 4, header = "Số lượng Đại lý phát sinh giao dịch - LK tháng")
    private Long totalAgentsMonth;

    @XlsxColumn(index = 5, header = "Số lượng Đại lý phát sinh giao dịch - LK năm")
    private Long totalAgentsYear;

    @XlsxColumn(index = 6, header = "Số lượng Thuê Bao Đấu Nối - Ngày")
    private Long totalEsimConnectedToday;

    @XlsxColumn(index = 7, header = "Số lượng Thuê Bao Đấu Nối - LK tháng")
    private Long totalEsimConnectedMonth;

    @XlsxColumn(index = 8, header = "Số lượng Thuê Bao Đấu Nối - So với tháng trước (%)")
    private Double totalEsimConnectedMonthGrowth;

    @XlsxColumn(index = 9, header = "Số lượng Thuê Bao Đấu Nối - LK năm")
    private Long totalEsimConnectedYear;

    @XlsxColumn(index = 10, header = "Số lượng Thuê Bao Mua Gói - Ngày")
    private Long totalEsimPurchasedToday;

    @XlsxColumn(index = 11, header = "Số lượng Thuê Bao Mua Gói - LK tháng")
    private Long totalEsimPurchasedMonth;

    @XlsxColumn(index = 12, header = "Số lượng Thuê Bao Mua Gói - So với tháng trước (%)")
    private Double totalEsimPurchasedMonthGrowth;

    @XlsxColumn(index = 13, header = "Số lượng Thuê Bao Mua Gói - LK năm")
    private Long totalEsimPurchasedYear;
}
