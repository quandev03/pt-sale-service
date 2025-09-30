package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ORGANIZATION_DELIVERY_INFO")
public class OrganizationDeliveryInfoEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "ORG_ID", nullable = false)
    private String orgId;

    @Column(name = "PROVINCE_CODE")
    private String provinceCode;

    @Column(name = "DISTRICT_CODE")
    private String districtCode;

    @Column(name = "WARD_CODE")
    private String wardCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CONSIGNEE_NAME")
    private String consigneeName;

    @Column(name = "CONSIGNEE_ADDRESS")
    private String consigneeAddress;

    @Column(name = "ORG_TITLE")
    private String orgTitle;

    @Column(name = "ID_NO")
    private String idNo;

    @Column(name = "ID_DATE")
    private String idDate;

    @Column(name = "ID_PLACE")
    private String idPlace;

    @Column(name = "ID_CARD_FRONT_SITE_FILE_URL")
    private String idCardFrontSiteFileUrl;

    @Column(name = "ID_CARD_BACK_SITE_FILE_URL")
    private String idCardBackSiteFileUrl;

    @Column(name = "MULTI_FILE_URL")
    private String multiFileUrl;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PASSPORT_NO")
    private String passportNo;

    @Column(name = "STATUS")
    private Boolean status;

}
