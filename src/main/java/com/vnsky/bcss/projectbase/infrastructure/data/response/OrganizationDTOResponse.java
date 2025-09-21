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
public class OrganizationDTOResponse {
    @DbColumnMapper("CHILD_ID")
    private Long id;

    @DbColumnMapper("CHILD_NAME")
    private String name;

    @DbColumnMapper("PARENT_ID")
    private Long parentId;

    @DbColumnMapper("PARENT_NAME")
    private String nameParent;

    @DbColumnMapper("IS_CURRENT")
    private Boolean isCurrent = Boolean.FALSE;

}
