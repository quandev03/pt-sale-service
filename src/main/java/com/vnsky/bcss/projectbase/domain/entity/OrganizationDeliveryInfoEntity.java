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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORGANIZATION_DELIVERY_INFO_id_gen")
    @SequenceGenerator(name = "ORGANIZATION_DELIVERY_INFO_id_gen", sequenceName = "ORGANIZATION_DELIVERY_INFO_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ORG_ID", nullable = false)
    private Long orgId;

    @Size(max = 10)
    @Column(name = "PROVINCE_CODE", length = 10)
    private String provinceCode;

    @Size(max = 10)
    @Column(name = "DISTRICT_CODE", length = 10)
    private String districtCode;

    @Size(max = 15)
    @Column(name = "WARD_CODE", length = 15)
    private String wardCode;

    @Size(max = 250)
    @Column(name = "ADDRESS", length = 250)
    private String address;

    @Size(max = 100)
    @Column(name = "CONSIGNEE_NAME", length = 100)
    private String consigneeName;

    @Column(name = "CONSIGNEE_ADDRESS")
    private String consigneeAddress;

    @Size(max = 100)
    @Column(name = "ORG_TITLE", length = 100)
    private String orgTitle;

    @Size(max = 30)
    @Column(name = "ID_NO", length = 30)
    private String idNo;

    @Size(max = 30)
    @Column(name = "ID_DATE", length = 30)
    private String idDate;

    @Size(max = 300)
    @Column(name = "ID_PLACE", length = 30)
    private String idPlace;

    @Column(name = "ID_CARD_FRONT_SITE_FILE_URL")
    private String idCardFrontSiteFileUrl;

    @Column(name = "ID_CARD_BACK_SITE_FILE_URL")
    private String idCardBackSiteFileUrl;

    @Column(name = "MULTI_FILE_URL")
    private String multiFileUrl;

    @Size(max = 1)
    @Column(name = "GENDER", length = 1)
    private String gender;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Size(max = 15)
    @Column(name = "PHONE", length = 15)
    private String phone;

    @Size(max = 30)
    @Column(name = "PASSPORT_NO", length = 30)
    private String passportNo;

    @Column(name = "STATUS")
    private Boolean status;

}
