package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExportQrEsimExcel {

    @XlsxColumn(index = 0, header = "Mã gói cước")
    @DbColumnMapper("PCK_CODE")
    private String productCode;

    @XlsxColumn(index = 1, header =  "Tên gói cước")
    @DbColumnMapper("PCK_NAME")
    private String productName;

    @XlsxColumn(index = 2, header = "Serial")
    @DbColumnMapper("SERIAL")
    private String serial;

    @XlsxColumn(index = 3, header = "Số thuê bao")
    @DbColumnMapper("ISDN")
    private Long isdn;

    @XlsxColumn(index = 4, header = "Mã QR", isImageColumn = true)
    @DbColumnMapper("LPA")
    private String lpa;

}
