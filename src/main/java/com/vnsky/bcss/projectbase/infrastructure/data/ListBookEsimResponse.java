package com.vnsky.bcss.projectbase.infrastructure.data;


import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
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
public class ListBookEsimResponse {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("ORG_ID")
    private String orgId;

    @DbColumnMapper("ORDER_NO")
    private String orderNo;

    @DbColumnMapper("AMOUNT_TOTAL")
    private BigDecimal amountTotal;

    @DbColumnMapper("REASON_ID")
    private String reasonId;

    @DbColumnMapper("DESCRIPTION")
    private String description;

    @DbColumnMapper("NOTE")
    private String note;

    @DbColumnMapper("ORDER_TYPE")
    private Integer orderType;

    @DbColumnMapper("CUSTOMER_EMAIL")
    private String customerEmail;

    @DbColumnMapper("QUANTITY")
    private BigDecimal quantity;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("CANCEL_REASON")
    private String cancelReason;

    @DbColumnMapper("ORDER_DATE")
    private LocalDateTime orderDate;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;

    @DbColumnMapper("MODIFIED_BY")
    private String modifiedBy;

    @DbColumnMapper("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @DbColumnMapper("SUCCESSED_NUMBER")
    private Integer successedNumber;

    @DbColumnMapper("FAILED_NUMBER")
    private Integer failedNumber;

    @DbColumnMapper("PACKAGE_CODES")
    private String packageCodes;
}
