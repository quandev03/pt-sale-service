package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActionHistoryDTO{

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("SUB_ID")
    private String subId;

    @DbColumnMapper("ACTION_DATE")
    private LocalDateTime actionDate;

    @DbColumnMapper("ACTION_CODE")
    private String actionCode;

    @DbColumnMapper("DESCRIPTION")
    private String description;

    @DbColumnMapper("SHOP_CODE")
    private String shopCode;

    @DbColumnMapper("EMP_CODE")
    private String empCode;

    @DbColumnMapper("EMP_NAME")
    private String empName;

    @DbColumnMapper("REASON_CODE")
    private String reasonCode;

    @DbColumnMapper("REASON_NOTE")
    private String reasonNote;

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;
}
