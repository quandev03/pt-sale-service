package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ContractResponse {
    String id;

    // Thông tin chung
    String contractLocation;
    String currentDay;
    String currentMonth;
    String currentYear;

    // Bên A - Chủ nhà
    String ownerName;
    String ownerBirth;
    String ownerPermanentAddress;
    String ownerId;
    String ownerIdIssueDay;
    String ownerIdIssueMonth;
    String ownerIdIssueYear;
    String ownerIdIssuePlace;
    String ownerPhone;

    // Bên B - Người thuê
    String tenantName;
    String tenantBirth;
    String tenantPermanentAddress;
    String tenantId;
    String tenantIdIssueDay;
    String tenantIdIssueMonth;
    String tenantIdIssueYear;
    String tenantIdIssuePlace;
    String tenantPhone;

    // Thông tin hợp đồng
    String roomAddress;
    String rentPrice;
    String paymentMethod;
    String electricPrice;
    String waterPrice;
    String depositAmount;
    String startDateDay;
    String startDateMonth;
    String startYear;
    String endDateDay;
    String endDateMonth;
    String endYear;
    String noticeDays;

    // Image URLs
    String frontImageUrl;
    String backImageUrl;
    String portraitImageUrl;
    String contractPdfUrl;

    // Audit fields
    String createdBy;
    LocalDateTime createdDate;
    String modifiedBy;
    LocalDateTime modifiedDate;
}

