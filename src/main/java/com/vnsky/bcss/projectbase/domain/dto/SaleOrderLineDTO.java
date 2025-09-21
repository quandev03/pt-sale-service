package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SaleOrderLineDTO extends AbstractAuditDTO {
    private String id;
    private String saleOrderId;
    private Long price;
    private Long version;
    private String note;
    private String subId;
}
