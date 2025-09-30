package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
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
    @DbColumnMapper("ID")
    private String id;
    @DbColumnMapper("SERIAL")
    private String serial;
    @DbColumnMapper("ISDN")
    private Long isdn;
    @DbColumnMapper("STATUS")
    private Integer status;
    @DbColumnMapper("IMSI")
    private Long imsi;
    @DbColumnMapper("ESIM_REGISTRATION_ID")
    private String esimRegistrationId;
    @DbColumnMapper("LPA")
    private String lpa;
    @DbColumnMapper("PCK_CODE")
    private String pckCode;
}
