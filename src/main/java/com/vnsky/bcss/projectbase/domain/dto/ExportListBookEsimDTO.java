package com.vnsky.bcss.projectbase.domain.dto;


import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportListBookEsimDTO {

    @XlsxColumn(index = 0, header = "Số lượng", ignoreIndex = 0, headerCsv = "so_luong")
    private BigDecimal quantity;

    @XlsxColumn(index = 1, header = "Mã đơn hàng", ignoreIndex = 1, headerCsv = "ma_don_hang")
    private String orderNo;

    @XlsxColumn(index = 2, header = "Mã gói cước", ignoreIndex = 2, headerCsv = "ma_goi_cuoc")
    private String packageCodes;

    @XlsxColumn(index = 3, header = "Tổng tiền gói cước", ignoreIndex = 3, headerCsv = "tong_tien_goi_cuoc")
    private BigDecimal amountTotal;

    @XlsxColumn(index = 4, header = "Người thực hiện", ignoreIndex = 4, headerCsv = "nguoi_thuc_hien")
    private String createdBy;

    @XlsxColumn(index = 5, header = "Thời gian thực hiện", ignoreIndex = 5, headerCsv = "thoi_gian_thuc_hien")
    private LocalDateTime createdDate;

    @XlsxColumn(index = 6, header = "Thời gian hoàn thành", ignoreIndex = 6, headerCsv = "thoi_gian_hoan_thanh")
    private LocalDateTime orderDate;

    @XlsxColumn(index = 7, header = "Trạng thái", ignoreIndex = 7, headerCsv = "trang_thai")
    private String status;

    @XlsxColumn(index = 8, header = "Số lượng thành công", ignoreIndex = 8, headerCsv = "so_luong_thanh_cong")
    private Integer successedNumber;

    @XlsxColumn(index = 9, header = "Số lượng thất bại", ignoreIndex = 9, headerCsv = "so_luong_that_bai")
    private Integer failedNumber;

}
