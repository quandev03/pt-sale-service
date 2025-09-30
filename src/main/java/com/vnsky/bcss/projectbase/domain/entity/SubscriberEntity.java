package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SUBSCRIBER")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SubscriberEntity extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ISDN")
    private Long isdn;

    @Column(name = "IMSI")
    private Long imsi;

    @Column(name = "SERIAL")
    private String serial;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "ID_NO_EXPIRED_DATE")
    private LocalDate idNoExpiredDate;

    @Column(name = "GENDER")
    private Integer gender;

    @Column(name = "ID_NUMBER")
    private String idNumber;

    @Column(name = "ID_NO_ISSUED_PLACE")
    private String idNoIssuedPlace;

    @Column(name = "NATIONALITY")
    private String nationality;

    @Column(name = "NICNUMBER")
    private String nicNumber;

    @Column(name = "PLACE_OF_BIRTH")
    private String placeOfBirth;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "VERIFIED_STATUS")
    private Integer verifiedStatus = 0;

    @Column(name = "ACTIVE_STATUS")
    private Integer activeStatus;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "STATUS_900")
    private Integer statusCall900 = 0;

    @Column(name = "BOUGHT_STATUS")
    private Integer boughtStatus = 0;

    @Column(name = "APP_OBJECT")
    private String appObject;

    @Column(name = "REG_DATE")
    private LocalDate regDate;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTION_ALLOW")
    private Integer actionAllow;

    @Column(name = "DECREE13_ACCEPT")
    private String decree13Accept;

    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "LPA")
    private String lpa;

    @Column(name = "PACK_CODE")
    private String packCode;

    @Column(name = "GEN_QR_BY")
    private String genQrBy;

    @Column(name = "DEVICE_NAME")
    private String deviceName;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "REGISTRATION_LINK")
    private String registrationLink;

    @Column(name = "LINK_EXPIRY_TIME")
    private LocalDateTime linkExpiryTime;

    @Column(name = "PASSPORT_URL")
    private String passportUrl;

    @Column(name = "PORTRAIT_URL")
    private String portraitUrl;

    @Column(name = "DECREE_13_PDF_URL")
    private String decree13PdfUrl;

    @Column(name = "DECREE_13_PNG_URL")
    private String decree13PngUrl;

    @Column(name = "CONTRACT_PDF_URL")
    private String contractPdfUrl;

    @Column(name = "CONTRACT_PNG_URL")
    private String contractPngUrl;

    @Column(name = "MSGID")
    private String msgId;

    @Column(name = "ESIM_GW_ID")
    private String esimGwId;

    @Column(name = "MBF_SUB_ID")
    private String mbfSubId;

    @Column(name = "CUSTOMER_CODE")
    private String customerCode;

    @Column(name = "CONTRACT_CODE")
    private String contractCode;

    @Column(name = "UPDATE_INFO_BY")
    private String updateInfoBy;

    @Column(name = "UPDATE_INFO_DATE")
    private LocalDateTime updateInfoDate;

    private String signatureUrl;

}
