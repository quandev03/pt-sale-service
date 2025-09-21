package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ESimDetailResponse {
    @DbColumnMapper("NATIONALITY")
    private String nationality;
    private String typeDocument;
    @DbColumnMapper("CONTRACT_CODE")
    private String contractCode;
    @DbColumnMapper("GENDER")
    private int gender;
    @DbColumnMapper("CUSTOMER_CODE")
    private String customerCode;
    @DbColumnMapper("FULL_NAME")
    private String fullName;
    @DbColumnMapper("DATE_OF_BIRTH")
    private LocalDateTime birthOfDate;
    @DbColumnMapper("ID_NO_EXPIRED_DATE")
    private LocalDateTime idNoExpireDate;
    @DbColumnMapper("ID_NO_ISSUED_PLACE")
    private String issuePlace;
    @DbColumnMapper("ID_NUMBER")
    private String idNumber;
}
