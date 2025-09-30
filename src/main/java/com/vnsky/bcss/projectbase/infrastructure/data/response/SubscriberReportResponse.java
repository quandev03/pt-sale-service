package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriberReportResponse {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("CONTRACT_CODE")
    private String contractCode;

    @DbColumnMapper("CUSTOMER_CODE")
    private String customerCode;

    @DbColumnMapper("FULL_NAME")
    private String fullName;

    @DbColumnMapper("ISDN")
    private Long isdn;

    @DbColumnMapper("SERIAL")
    private String serial;

    @DbColumnMapper("PACK_CODE")
    private String packCode;

    @DbColumnMapper("ID_NUMBER")
    private String idNumber;

    @DbColumnMapper("ACTIVE_STATUS")
    private Integer activeStatus;

    @DbColumnMapper("NATIONALITY")
    private String nationality;

    @DbColumnMapper("GENDER")
    private Integer gender;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @DbColumnMapper("UPDATE_INFO_BY")
    private String updateInfoBy;

    @DbColumnMapper("UPDATE_INFO_DATE")
    private LocalDateTime updateInfoDate;
}
