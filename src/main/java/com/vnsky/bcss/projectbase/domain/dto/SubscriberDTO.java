package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SubscriberDTO extends AbstractAuditDTO{
    private String id;

    private String serial;

    private Long isdn;

    private Long imsi;

    private Integer status;

    private String registrationLink;

    private LocalDateTime linkExpiryTime;

    private LocalDate dateOfBirth;

    private LocalDate idNoExpiredDate;

    private Integer gender;

    private String idNumber;

    private String idNoIssuedPlace;

    private String nationality;

    private String nicNumber;

    private String placeOfBirth;

    private String fullName;

    private String packCode;

    private String lpa;

    private Integer statusCall900;

    private Integer verifiedStatus;

    private String passportUrl;

    private String portraitUrl;

    private String decree13PdfUrl;

    private String decree13PngUrl;

    private String contractPdfUrl;

    private String contractPngUrl;

    private String mbfSubId;

    private String msgId;

    private String orgId;

    private Integer activeStatus;

    private LocalDate regDate;

    private String esimGwId;

    private String customerCode;

    private String contractCode;

    private String genQrBy;

    private String signatureUrl;

    private String clientId;

    private Integer boughtStatus;

    private String updateInfoBy;

    private LocalDateTime updateInfoDate;
}
