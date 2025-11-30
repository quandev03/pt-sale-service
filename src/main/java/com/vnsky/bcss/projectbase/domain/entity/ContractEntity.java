package com.vnsky.bcss.projectbase.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "CONTRACTS")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContractEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Thông tin chung
    @Column(name = "CONTRACT_LOCATION")
    private String contractLocation;

    @Column(name = "CURRENT_DAY")
    private String currentDay;

    @Column(name = "CURRENT_MONTH")
    private String currentMonth;

    @Column(name = "CURRENT_YEAR")
    private String currentYear;

    // Bên A - Chủ nhà
    @Column(name = "OWNER_NAME")
    private String ownerName;

    @Column(name = "OWNER_BIRTH")
    private String ownerBirth;

    @Column(name = "OWNER_PERMANENT_ADDRESS")
    private String ownerPermanentAddress;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "OWNER_ID_ISSUE_DAY")
    private String ownerIdIssueDay;

    @Column(name = "OWNER_ID_ISSUE_MONTH")
    private String ownerIdIssueMonth;

    @Column(name = "OWNER_ID_ISSUE_YEAR")
    private String ownerIdIssueYear;

    @Column(name = "OWNER_ID_ISSUE_PLACE")
    private String ownerIdIssuePlace;

    @Column(name = "OWNER_PHONE")
    private String ownerPhone;

    // Bên B - Người thuê
    @Column(name = "TENANT_NAME")
    private String tenantName;

    @Column(name = "TENANT_BIRTH")
    private String tenantBirth;

    @Column(name = "TENANT_PERMANENT_ADDRESS")
    private String tenantPermanentAddress;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "TENANT_ID_ISSUE_DAY")
    private String tenantIdIssueDay;

    @Column(name = "TENANT_ID_ISSUE_MONTH")
    private String tenantIdIssueMonth;

    @Column(name = "TENANT_ID_ISSUE_YEAR")
    private String tenantIdIssueYear;

    @Column(name = "TENANT_ID_ISSUE_PLACE")
    private String tenantIdIssuePlace;

    @Column(name = "TENANT_PHONE")
    private String tenantPhone;

    // Thông tin hợp đồng
    @Column(name = "ROOM_ADDRESS")
    private String roomAddress;

    @Column(name = "RENT_PRICE")
    private String rentPrice;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "ELECTRIC_PRICE")
    private String electricPrice;

    @Column(name = "WATER_PRICE")
    private String waterPrice;

    @Column(name = "DEPOSIT_AMOUNT")
    private String depositAmount;

    @Column(name = "START_DATE_DAY")
    private String startDateDay;

    @Column(name = "START_DATE_MONTH")
    private String startDateMonth;

    @Column(name = "START_YEAR")
    private String startYear;

    @Column(name = "END_DATE_DAY")
    private String endDateDay;

    @Column(name = "END_DATE_MONTH")
    private String endDateMonth;

    @Column(name = "END_YEAR")
    private String endYear;

    @Column(name = "NOTICE_DAYS")
    private String noticeDays;

    // Image URLs
    @Column(name = "FRONT_IMAGE_URL")
    private String frontImageUrl;

    @Column(name = "BACK_IMAGE_URL")
    private String backImageUrl;

    @Column(name = "PORTRAIT_IMAGE_URL")
    private String portraitImageUrl;

    @Column(name = "CONTRACT_PDF_URL")
    private String contractPdfUrl;
}

