package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Summary By Organization Excel export
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryByOrgExcelDTO {

    @XlsxColumn(index = 0, header = "STT")
    private Integer stt;

    @XlsxColumn(index = 1, header = "Công ty khu vực")
    private String rootOrgName;

    @XlsxColumn(index = 2, header = "Tên Đại lý")
    private String orgName;

    @XlsxColumn(index = 3, header = "Số lượng thuê bao đặt hàng - Ngày")
    private Long totalEsimOrderedToday;

    @XlsxColumn(index = 4, header = "Số lượng thuê bao đặt hàng - LK tháng")
    private Long totalEsimOrderedMonth;

    @XlsxColumn(index = 5, header = "Số lượng thuê bao đặt hàng - So với tháng trước (%)")
    private Double totalEsimOrderedMonthGrowth;

    @XlsxColumn(index = 6, header = "Số lượng thuê bao đặt hàng - LK năm")
    private Long totalEsimOrderedYear;

    @XlsxColumn(index = 7, header = "Số lượng Thuê Bao kích hoạt 900 - Ngày")
    private Long totalEsimActivated900Today;

    @XlsxColumn(index = 8, header = "Số lượng Thuê Bao kích hoạt 900 - LK tháng")
    private Long totalEsimActivated900Month;

    @XlsxColumn(index = 9, header = "Số lượng Thuê Bao kích hoạt 900 - So với tháng trước (%)")
    private Double totalEsimActivated900MonthGrowth;

    @XlsxColumn(index = 10, header = "Số lượng Thuê Bao kích hoạt 900 - LK năm")
    private Long totalEsimActivated900Year;

    @XlsxColumn(index = 11, header = "Doanh thu gói cước - Ngày")
    private BigDecimal totalRevenueToday;

    @XlsxColumn(index = 12, header = "Doanh thu gói cước - LK tháng")
    private BigDecimal totalRevenueMonth;

    @XlsxColumn(index = 13, header = "Doanh thu gói cước - So với tháng trước (%)")
    private Double totalRevenueMonthGrowth;

    @XlsxColumn(index = 14, header = "Doanh thu gói cước - LK năm")
    private BigDecimal totalRevenueYear;
}
