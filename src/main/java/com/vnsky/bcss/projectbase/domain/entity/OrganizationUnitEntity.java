package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ORGANIZATION_UNIT")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrganizationUnitEntity extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "ORG_CODE")
    private String orgCode;

    @Column(name = "ORG_NAME")
    private String orgName;

    @Column(name = "ORG_TYPE")
    private String orgType;

    @Column(name = "ORG_SUB_TYPE")
    private String orgSubType;

    @Column(name = "ORG_DESCRIPTION")
    private String orgDescription;

    @Column(name = "PROVINCE_CODE")
    private String provinceCode;

    @Column(name = "DISTRICT_CODE")
    private String districtCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "TAX_CODE")
    private String taxCode;

    @Column(name = "CONTRACT_NO")
    private String contractNo;

    @Column(name = "CONTRACT_DATE")
    private LocalDate contractDate;

    @Column(name = "REPRESENTATIVE")
    private String representative;

    @Column(name = "WARD_CODE")
    private String wardCode;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "CONTRACT_NO_FILE_URL")
    private String contractNoFileUrl;

    @Column(name = "BUSINESS_LICENSE_FILE_URL")
    private String businessLicenseFileUrl;

    @Column(name = "BUSINESS_LICENSE_NO")
    private String businessLicenseNo;

    @Column(name = "BUSINESS_LICENSE_ADDRESS")
    private String businessLicenseAddress;

    @Column(name = "ORG_PARTNER_TYPE")
    private Integer orgPartnerType;

    @Column(name = "ORG_BANK_ACCOUNT_NO")
    private String orgBankAccountNo;

    @Column(name = "APPROVAL_STATUS")
    private Integer approvalStatus;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "PARENT_CODE")
    private String parentCode;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "DEBT_LIMIT")
    private Long debtLimit;

    @Column(name = "DEBT_LIMIT_MBF")
    private Long debtLimitMbf;

    @Column(name = "CCCD")
    private String cccd;
}
