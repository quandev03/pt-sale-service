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
public class OrderRevenueReportResponse {
    @DbColumnMapper
    private String id;

    @DbColumnMapper("ORDER_NO")
    private String orderNo;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_Name")
    private String orgName;

    @DbColumnMapper("ORDER_TYPE")
    private Integer orderType;

    @DbColumnMapper("AMOUNT_TOTAL")
    private BigDecimal amountTotal;

    @DbColumnMapper("QUANTITY")
    private BigDecimal quantity;

    @DbColumnMapper("SUCCEEDED_NUMBER")
    private Integer succeededNumber;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("ORDER_DATE")
    private LocalDateTime orderDate;
}
