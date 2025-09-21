package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPartnerResponse {

    @DbColumnMapper("ID")
    private Long id;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_SUB_TYPE")
    private String orgPartnerType;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;

    @DbColumnMapper("MODIFIED_BY")
    private String updatedBy;

    @DbColumnMapper("MODIFIED_DATE")
    private Instant updatedDate;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("APPROVAL_STATUS")
    private Integer approvalStatus;
}
