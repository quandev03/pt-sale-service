package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExportEsimExcelDTO {
    @XlsxColumn(index = 0, header = "Số thuê bao", ignoreIndex = 0, headerCsv = "So the bao")
    private Long isdn;

    @XlsxColumn(index = 1, header = "Serial SIM", ignoreIndex = 1, headerCsv = "Serial Sim")
    private String serial;

    @XlsxColumn(index = 2, header = "Gói cước", ignoreIndex = 2, headerCsv = "Ma goi cuoc")
    private String packageCode;

    @XlsxColumn(index = 3, header = "Mã đơn hàng", ignoreIndex = 3, headerCsv = "Ma don hang")
    private String orderNo;

    @XlsxColumn(index = 4, header = "Đại lý", ignoreIndex = 4, headerCsv = "Ten dai ly")
    private String orgName;

    @XlsxColumn(index = 5, header = "Trạng thái thuê bao", ignoreIndex = 5, headerCsv = "Trang thai thue bao")
    private String subStatus;

    @XlsxColumn(index = 6, header = "Trạng thái chặn cắt", ignoreIndex = 6, headerCsv = "Trang thai chan cat")
    private String activeStatus;

    @XlsxColumn(index = 7, header = "Người gen QR", ignoreIndex = 7, headerCsv = "Nguoi gen qr code")
    private String genQrBy;

    @XlsxColumn(index = 9, header = "Thời gian cập nhật", ignoreIndex = 8, headerCsv = "Thoi gian cap nhat")
    private String modifiedDate;

    @XlsxColumn(index = 10, header = "Mã QR Code", isImageColumn = true)
    private String lpaCode;

    @XlsxColumn(index = 8, header = "Thời gian đặt hàng")
    private String createdDate;
}
