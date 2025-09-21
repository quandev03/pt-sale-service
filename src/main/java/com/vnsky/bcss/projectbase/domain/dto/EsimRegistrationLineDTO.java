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
public class EsimRegistrationLineDTO extends AbstractAuditDTO {
    private String id;
    private String serial;
    private Long isdn;
    private Integer status;
    private Long imsi;
    private String saleOrderId;
    private String lpa;
} 