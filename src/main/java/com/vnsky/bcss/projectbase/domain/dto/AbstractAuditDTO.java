package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractAuditDTO implements Serializable {

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;

    @DbColumnMapper("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("MODIFIED_BY")
    private String modifiedBy;
}
