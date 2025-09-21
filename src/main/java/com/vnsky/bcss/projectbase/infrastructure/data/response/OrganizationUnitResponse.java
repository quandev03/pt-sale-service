package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUnitResponse {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("PARENT_ID")
    private String parentId;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;
}
