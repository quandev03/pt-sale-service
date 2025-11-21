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
public class OrganizationUnitImageDTO extends AbstractAuditDTO {
    private String id;
    private String orgUnitId;
    private String imageUrl;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private Integer sortOrder;
}

