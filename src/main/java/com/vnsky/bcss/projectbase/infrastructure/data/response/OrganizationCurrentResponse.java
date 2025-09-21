package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCurrentResponse {

    @DbColumnMapper("ORG_ID")
    private Long orgId;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("IS_CURRENT")
    private Boolean isCurrent;
}
