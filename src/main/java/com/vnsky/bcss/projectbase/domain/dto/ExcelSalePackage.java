package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExcelSalePackage {
    @XlsxColumn(index = 0, header = "Số thuê bao", headerCsv = "so thue bao", ignoreIndex = 0)
    private String phoneNumber;

    @XlsxColumn(index = 1, header = "Mã gói cước", headerCsv = "ma goi cuoc", ignoreIndex = 1)
    private String pckCode;

    @XlsxColumn(index = 2, header = "Kết Quả", headerCsv = "ket qua", ignoreIndex = 2)
    private String result;

    @XlsxColumn(index = 3, header = "Lý do thất bại", ignoreIndex = 3)
    private String failReason;

    private Long price;

    private SalePackageDTO salePackage;

    public void setFailResult(String reason){
        result = Constant.UploadFile.SalePackageBatch.FAIL;
        failReason = reason;
    }
}
