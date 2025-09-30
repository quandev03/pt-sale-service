package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    private Long amountTotal;
    private String reasonId;
    private String description;
    private String note;
    private Integer orderType;
    private String customerEmail;
    private Long quantity;
    private String cancelReason;
    private LocalDateTime orderDate;
}
