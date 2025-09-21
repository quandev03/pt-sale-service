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
public class OrganizationUserDTO extends AbstractAuditDTO {
    private String id;
    private String orgId;
    private String userId;
    private Integer isCurrent;
    private String userName;
    private String userFullname;
    private String clientId;
    private Integer status;
    private String email;
}
