package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageReportResponse {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("FILE_URL")
    private String fileUrl;

    @DbColumnMapper("FILE_NAME")
    private String fileName;

    @DbColumnMapper("ISDN")
    private String isdn;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("PCK_CODE")
    private String pckCode;

    @DbColumnMapper("AMOUNT_TOTAL")
    private BigDecimal amountTotal;

    @DbColumnMapper("TYPE")
    private Integer type;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("ORDER_DATE")
    private LocalDateTime orderDate;
}
