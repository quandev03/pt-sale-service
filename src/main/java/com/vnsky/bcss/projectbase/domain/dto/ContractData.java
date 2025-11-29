package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractData {
    // Thông tin chung
    private String contractLocation;
    private String currentDay;
    private String currentMonth;
    private String currentYear;

    // Bên A - Chủ nhà
    private String ownerName;
    private String ownerBirth;
    private String ownerPermanentAddress;
    private String ownerId;
    private String ownerIdIssueDay;
    private String ownerIdIssueMonth;
    private String ownerIdIssueYear;
    private String ownerIdIssuePlace;
    private String ownerPhone;

    // Bên B - Người thuê
    private String tenantName;
    private String tenantBirth;
    private String tenantPermanentAddress;
    private String tenantId;
    private String tenantIdIssueDay;
    private String tenantIdIssueMonth;
    private String tenantIdIssueYear;
    private String tenantIdIssuePlace;
    private String tenantPhone;

    // Thông tin hợp đồng
    private String roomAddress;
    private String rentPrice;
    private String paymentMethod;
    private String electricPrice;
    private String waterPrice;
    private String depositAmount;
    private String startDateDay;
    private String startDateMonth;
    private String startYear;
    private String endDateDay;
    private String endDateMonth;
    private String endYear;
    private String noticeDays;
}
