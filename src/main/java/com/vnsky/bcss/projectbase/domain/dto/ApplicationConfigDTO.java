package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class ApplicationConfigDTO extends AbstractAuditDTO{
    private String id;

    private String type;

    private String code;

    private String name;

    private String dataType;

    private String value;

    private Integer status;

    private String language;
}
