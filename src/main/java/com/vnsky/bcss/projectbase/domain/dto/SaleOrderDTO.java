package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SaleOrderDTO extends AbstractAuditDTO {
    private String id;
    private String orgId;
    private String orderNo;
    private BigDecimal amountTotal;
    private Integer status;
    private String reasonId;
    private String description;
    private String note;
    private Integer orderType;
    private String customerEmail;
    private BigDecimal quantity;
    private String cancelReason;
    private Integer succeededNumber;
    private Integer failedNumber;
    private LocalDateTime finishedDate;
    private Integer bookEsimStatus;
    private String pckCode;
    private LocalDateTime orderDate;
}
