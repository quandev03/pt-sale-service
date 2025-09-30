package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.*;
import com.vnsky.bcss.projectbase.domain.port.secondary.ActionHistoryRepoPort;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.ActionHistoryActionCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.constant.SaleOrderConstant;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.security.SecurityUtil;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.RegisterPackageMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.RegisterPackageMbfResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.CheckStatusMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.CheckStatusMbfResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
@RequiredArgsConstructor
public abstract class BasePackageServicePort {
    protected final PackageProfileRepoPort packageProfileRepoPort;
    protected final SubscriberRepoPort subscriberRepoPort;
    protected final BatchPackageSaleRepoPort batchPackageSaleRepoPort;
    protected final SaleOrderRepoPort saleOrderRepoPort;
    protected final SaleOrderLineRepoPort saleOrderLineRepoPort;
    protected final OrganizationUserRepoPort organizationUserRepoPort;
    protected final MinioOperations minioOperations;
    protected final RestTemplate restTemplate;
    protected final IntegrationPort integrationPort;
    protected final KafkaProducerPort kafkaProducerPort;
    protected final OrganizationUnitRepoPort organizationUnitRepoPort;
    protected final ActionHistoryRepoPort actionHistoryRepoPort;
    protected final OrganizationUnitServicePort organizationUnitServicePort;

    private static final String SUCCESS_CODE = "0000";
    private static final int MAX_LIMIT_ROW_COUNT = 10000;
    private static final int ACTUAL_PAYMENT = 1;

    public abstract Object registerPackage(SalePackageDTO salePackage);

    public abstract List<ExcelSalePackage> checkData(MultipartFile attachment);

    public abstract Object submitData(MultipartFile attachment);

    // Helper methods that can be used by concrete implementations
    protected void checkLimit(String pckCode, Long price) {
        // Implementation for checking organization limits with admin service
        log.debug("Checking limit for package: {} with price: {}", pckCode, price);
        checkOrganizationUnitLimit(price);
    }

    protected void checkLimitExcel(Long price) {
        // Check limit for Excel batch processing
        log.debug("Checking Excel batch limit for price: {}", price);
        checkOrganizationUnitLimit(price);
    }

    protected void checkOrganizationUnitLimit(Long price) {
        OrganizationUnitDTO orgUnitDTO = organizationUnitServicePort.getOrgCurrent();
        Long debitLimit = Optional.ofNullable(orgUnitDTO.getDebtLimit()).orElse(0L);
        Long debitLimitMbf = Optional.ofNullable(orgUnitDTO.getDebtLimitMbf()).orElse(0L);

        if (debitLimit > 0L) {
            log.debug("debitLimit: {}, debitLimitMbf: {}", debitLimit, debitLimitMbf);
            validateDebitLimitAgainstClientLimit(price, debitLimit);
        } else {
            throw BaseException.badRequest(ErrorCode.CLIENT_LIMIT_RETRIEVAL_FAILED)
                .message("Dữ liệu giới hạn khách hàng không hợp lệ")
                .build();
        }

        log.debug("Organization Unit limit check passed for orgId: {} with debit limit: {}", orgUnitDTO.getId(), debitLimit);
    }

    private void validateDebitLimitAgainstClientLimit(Long price, Long debitLimit) {
        if (price - debitLimit > 0)
            throw BaseException.badRequest(ErrorCode.CLIENT_LIMIT_EXCEEDED).build();

        log.debug("Debit limit validation passed. Requested: {}, Available: {}", price, debitLimit);
    }

    protected void registerPackageByIsdn(String isdn, String packageCode) {
        // Pre-check status with MBF by isdn and serial and get current package code if any
        Optional<String> precheckedPckCode = precheckSubscriberStatus(isdn);
        deleteCurrentPackageIfNeeded(isdn, precheckedPckCode.orElse(null));

        if (packageCode == null || packageCode.isEmpty()) {
            return;
        }

        performPackageRequest(isdn, List.of(packageCode), null, "Không thể đăng ký gói cước");

        // Record action history for package registration
        recordPackageActionHistory(isdn, ActionHistoryActionCode.REGISTER_PACKAGE, packageCode);
    }

    private Optional<String> precheckSubscriberStatus(String isdn) {
        Optional<SubscriberDTO> subscriberOpt = getSubscriberByIsdn(isdn);
        if (subscriberOpt.isEmpty()) {
            return Optional.empty();
        }

        SubscriberDTO subscriber = subscriberOpt.get();
        String serial = subscriber.getSerial();
        if (serial == null || serial.trim().isEmpty()) {
            return Optional.empty();
        }

        CheckStatusMbfRequest request = CheckStatusMbfRequest.builder()
            .isdn(isdn)
            .serial(serial)
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.CHECK_STATUS, null, request);

        CheckStatusMbfResponse response = integrationPort.executeRequestWithRetry(integrationRequest, CheckStatusMbfResponse.class);
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            log.warn("CHECK_STATUS precheck returned no data for ISDN {} (serial {})", isdn, serial);
            return Optional.empty();
        }

        // Find matching item by ISDN if present; otherwise use first item
        CheckStatusMbfResponse.CheckStatusItem item = response.getData().stream()
            .filter(i -> isdn.equals(i.getIsdn()))
            .findFirst()
            .orElse(response.getData().get(0));

        String rawPckCode = item.getPckCode();
        if (rawPckCode == null || rawPckCode.trim().isEmpty()) {
            return Optional.empty();
        }

        // Normalize pckCode by removing any entries containing HVN0 and trimming spaces
        String selected = Arrays.stream(rawPckCode.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .filter(s -> !s.contains("HVN0"))
            .findFirst()
            .orElse("");

        if (selected.isEmpty()) {
            return Optional.empty();
        }

        log.debug("Precheck detected current package for ISDN {}: {} (from raw '{}')", isdn, selected, rawPckCode);
        return Optional.of(selected);
    }

    private void deleteCurrentPackageIfNeeded(String isdn, String currentPack) {
        if (currentPack == null || currentPack.trim().isEmpty()) {
            return;
        }

        if (!isNonDefaultPackage(currentPack)) {
            return;
        }

        performPackageRequest(isdn, null, List.of(currentPack), "Không thể huỷ gói cước hiện tại");

        // Record action history for package deletion
        recordPackageActionHistory(isdn, ActionHistoryActionCode.DELETE_PACKAGE, currentPack);
    }

    private Optional<SubscriberDTO> getSubscriberByIsdn(String isdn) {
        try {
            return subscriberRepoPort.findByLastIsdn(Long.valueOf(isdn));
        } catch (Exception e) {
            log.warn("Không thể lấy thông tin thuê bao {}: {}", isdn, e.getMessage());
            return Optional.empty();
        }
    }

    private boolean isNonDefaultPackage(String packCode) {
        if (packCode == null || packCode.trim().isEmpty()) {
            return false;
        }

        try {
            Optional<PackageProfileDTO> packageOpt = packageProfileRepoPort.findByPckCode(packCode);
            if (packageOpt.isEmpty()) {
                throw BaseException.badRequest(ErrorCode.PACKAGE_NOT_EXISTS).build();
            }

            PackageProfileDTO packageProfile = packageOpt.get();
            Long price = packageProfile.getPackagePrice();

            return price != null && price > 0;
        } catch (Exception e) {
            log.warn("Error checking package price for {}: {}", packCode, e.getMessage());
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void performPackageRequest(String isdn, List<String> regPackages, List<String> delPackages, String genericErrorMessage) {
        RegisterPackageMbfRequest request = RegisterPackageMbfRequest.builder()
            .strIsdn(isdn)
            .strShopCode(SecurityUtil.getCurrentClientCode())
            .strEmployee(getCurrentEmployeeOrThrow(SecurityUtil.getCurrentUserId()))
            .arrRegPck(regPackages)
            .arrDelPck(delPackages)
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.REGISTER_PACKAGE_TYPE, null, request);

        RegisterPackageMbfResponse response = integrationPort.executeRequestWithRetry(integrationRequest, RegisterPackageMbfResponse.class);

        validateMbfResponseOrThrow(response, genericErrorMessage);
    }

    private String getCurrentEmployeeOrThrow(String currentUserId) {
        OrganizationUserDTO organizationUserDTO = getCurrentOrganizationUserOrThrow(currentUserId);
        return organizationUnitRepoPort.findById(organizationUserDTO.getOrgId())
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.INVALID_USER).build()).getEmployeeCode();
    }

    private void validateMbfResponseOrThrow(RegisterPackageMbfResponse response, String genericErrorMessage) {
        if (response == null) {
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message(genericErrorMessage + " - không có phản hồi từ hệ thống")
                .build();
        }

        if (Objects.equals(SUCCESS_CODE, response.getCode())) {
            return;
        }

        String errorMessage = response.getDescription();
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            throw BaseException.badRequest(ErrorCode.INVALID_INPUT)
                .message(errorMessage)
                .build();
        }

        throw BaseException.badRequest(ErrorCode.INVALID_INPUT)
            .message(genericErrorMessage)
            .build();
    }

    /**
     * Create sale order for a single package registration (no line yet).
     * The caller should create sale order line ONLY after external registration succeeds.
     */
    protected SaleOrderDTO createSaleOrderForSinglePackage(String isdn, String pckCode, Long price) {
        try {
            String currentUserId = SecurityUtil.getCurrentUserId();
            OrganizationUserDTO organizationUser = getCurrentOrganizationUserOrThrow(currentUserId);

            Long safePrice = price == null ? 0L : price;

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setOrgId(organizationUser.getOrgId());
            saleOrder.setOrderType(SaleOrderConstant.BATCH_PACKAGE);
            saleOrder.setAmountTotal(safePrice);
            saleOrder.setQuantity(1L);
            saleOrder.setOrderDate(LocalDateTime.now());
            saleOrder.setDescription("Đăng ký gói cho số " + isdn);
            saleOrder.setNote("Single package registration");

            // Save the sale order
            SaleOrderDTO savedSaleOrder = saleOrderRepoPort.saveAndFlush(saleOrder);

            log.info("Created single package sale order: Order ID={}, for ISDN={}, Package={} ",
                savedSaleOrder.getId(), isdn, pckCode);

            return savedSaleOrder;

        } catch (Exception e) {
            log.error("Error creating sale order for single package: isdn={}, pckCode={}", isdn, pckCode, e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi tạo đơn hàng cho đăng ký gói cước")
                .build();
        }
    }

    /**
     * Create a sale order line for a given sale order, used after successful registration
     */
    protected void createSaleOrderLine(String saleOrderId, String isdn, String pckCode, Long price) {
        try {
            Long safePrice = price == null ? 0L : price;
            Long safeQuantity = 1L;

            SaleOrderLineDTO saleOrderLine = SaleOrderLineDTO.builder()
                .saleOrderId(saleOrderId)
                .price(safePrice)
                .quantity(safeQuantity)
                .note("Package registration")
                .pckCode(pckCode)
                .isdn(isdn)
                .payStatus(ACTUAL_PAYMENT)
                .build();

            SaleOrderLineDTO saved = saleOrderLineRepoPort.saveAndFlush(saleOrderLine);
            log.info("Created sale order line: Order ID={}, Line ID={}, ISDN={}, Package={}",
                saleOrderId, saved.getId(), isdn, pckCode);

        } catch (Exception e) {
            log.error("Error creating sale order line for orderId={}, isdn={}, pckCode={}", saleOrderId, isdn, pckCode, e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi tạo dòng đơn hàng")
                .build();
        }
    }

    /**
     * Get authorized package codes for the current partner
     */
    protected Set<String> getAuthorizedPckCode() {
        String currentClientId = SecurityUtil.getCurrentClientId();
        return packageProfileRepoPort.getListPackageProfile(currentClientId)
            .stream()
            .map(PackageProfileDTO::getPckCode)
            .collect(Collectors.toSet());
    }

    /**
     * Retrieve package price and ensure it is a positive value (> 0).
     * Throws a 400 error if the package has no price or price equals 0.
     */
    protected Long getPackagePriceOrThrowIfInvalid(String packageCode) {
        try {
            PackageProfileDTO packageProfile = packageProfileRepoPort.findByPackageCode(packageCode);
            Long price = (packageProfile != null) ? packageProfile.getPackagePrice() : null;

            if (price == null || price <= 0L) {
                throw BaseException.badRequest(ErrorCode.PACKAGE_PRICE_MUST_BE_GREATER_THAN_ZERO)
                    .build();
            }

            return price;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Could not validate price for package {}", packageCode, e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi kiểm tra giá gói cước")
                .build();
        }
    }

    /**
     * Convert phone number to ISDN by removing leading zeros
     */
    protected String convertPhoneToIsdn(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }

        String trimmedPhone = phoneNumber.trim();

        // Normalize supported formats to 9-digit ISDN:
        //  - 84 + 9 digits => drop leading 84
        //  - 0 + 9 digits  => drop leading 0
        //  - 9 digits      => keep as is
        String isdn = trimmedPhone;
        if (trimmedPhone.matches("^84\\d{9}$")) {
            isdn = trimmedPhone.substring(2);
        } else if (trimmedPhone.matches("^0\\d{9}$")) {
            isdn = trimmedPhone.substring(1);
        }

        log.debug("Converted phone number: {} -> ISDN: {}", phoneNumber, isdn);
        return isdn;
    }

    /**
     * Check if subscriber exists
     */
    protected boolean checkIsdn(String isdn) {
        try {
            log.debug("Checking if subscriber exists for ISDN: {}", isdn);

            Optional<SubscriberDTO> subscriber = subscriberRepoPort.findByLastIsdn(Long.valueOf(isdn));
            boolean subscriberExists = subscriber.isPresent();

            log.debug("Subscriber existence check result: {} for ISDN: {}", subscriberExists, isdn);
            return subscriberExists;

        } catch (Exception e) {
            log.error("Error checking subscriber existence for ISDN: {}", isdn, e);
            return false;
        }
    }

    /**
     * Update subscriber packCode after successful package registration
     */
    protected void updateSubscriberPackCode(String isdn, String pckCode) {
        try {
            Optional<SubscriberDTO> subscriberOpt = subscriberRepoPort.findByLastIsdn(Long.valueOf(isdn));
            if (subscriberOpt.isPresent()) {
                SubscriberDTO subscriber = subscriberOpt.get();
                subscriber.setPackCode(pckCode);
                subscriberRepoPort.saveAndFlush(subscriber);
                log.info("Updated subscriber packCode for ISDN {} to {}", isdn, pckCode);
            } else {
                log.warn("Subscriber not found to update packCode for ISDN {}", isdn);
            }
        } catch (Exception e) {
            log.warn("Failed to update subscriber packCode for ISDN {}: {}", isdn, e.getMessage());
        }
    }

    protected void publishDebit(Long amount) {
        try {
            if (amount == null || amount <= 0) {
                return;
            }
            DebitMessageDTO message = DebitMessageDTO.builder()
                .clientId(SecurityUtil.getCurrentClientId())
                .debitAmount(BigDecimal.valueOf(amount))
                .build();
            kafkaProducerPort.publishDebitMessage(message);
        } catch (Exception e) {
            log.warn("Failed to publish debit message: {}", e.getMessage());
        }
    }

    /**
     * Record action history for package operations
     */
    protected void recordPackageActionHistory(String isdn, ActionHistoryActionCode actionCode, String packageCode) {
        try {
            // Get subscriber ID from ISDN
            Optional<SubscriberDTO> subscriberOpt = getSubscriberByIsdn(isdn);
            if (subscriberOpt.isEmpty()) {
                log.warn("Cannot record action history - subscriber not found for ISDN: {}", isdn);
                return;
            }

            String subscriberId = subscriberOpt.get().getId();
            String description = actionCode.getDescription() + " " + packageCode + " cho số " + isdn;

            ActionHistoryDTO actionHistory = ActionHistoryDTO.builder()
                .subId(subscriberId)
                .actionDate(LocalDateTime.now())
                .actionCode(actionCode.getCode())
                .description(description)
                .shopCode(null)
                .empCode(null)
                .empName(null)
                .reasonCode("VIEW")
                .reasonNote(null)
                .createdBy(SecurityUtil.getCurrentUsername())
                .createdDate(LocalDateTime.now())
                .build();

            actionHistoryRepoPort.saveAndFlush(actionHistory);
            log.info("Recorded action history for ISDN: {} with action: {} package: {}", isdn, actionCode.getCode(), packageCode);
        } catch (Exception e) {
            log.warn("Failed to record action history for ISDN: {} action: {} package: {}: {}", isdn, actionCode.getCode(), packageCode, e.getMessage());
        }
    }

    /**
     * Upload file to MinIO
     */
    protected String uploadFileToMinio(byte[] fileBytes, String fileName) {
        try {
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(Constant.MinioDir.BatchPackageSale.RESULTS_FOLDER + Constant.CommonSymbol.FORWARD_SLASH + fileName)
                .isPublic(false)
                .build();

            minioOperations.upload(new ByteArrayInputStream(fileBytes), uploadOptionDTO);

            log.debug("File uploaded to MinIO successfully: {}/{}", Constant.MinioDir.BatchPackageSale.RESULTS_FOLDER, fileName);
            return uploadOptionDTO.getUri();

        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", fileName, e);
            throw BaseException.badRequest(ErrorCode.ERROR_UPLOAD_FILE_TO_MINIO)
                .message("Lỗi tải file lên MinIO")
                .build();
        }
    }

    /**
     * Upload MultipartFile to MinIO
     */
    protected String uploadFileToMinio(MultipartFile file, String fileName) {
        try {
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(Constant.MinioDir.BatchPackageSale.INPUT_FOLDER + Constant.CommonSymbol.FORWARD_SLASH + fileName)
                .isPublic(false)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOptionDTO);

            log.debug("File uploaded to MinIO successfully: {}/{}", Constant.MinioDir.BatchPackageSale.INPUT_FOLDER, fileName);
            return uploadOptionDTO.getUri();

        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", fileName, e);
            throw BaseException.badRequest(ErrorCode.ERROR_UPLOAD_FILE_TO_MINIO)
                .message("Lỗi tải file lên MinIO")
                .build();
        }
    }

    /**
     * Get current organization user or throw exception
     */
    protected OrganizationUserDTO getCurrentOrganizationUserOrThrow(String currentUserId) {
        if (currentUserId == null) {
            throw BaseException.badRequest(ErrorCode.INVALID_USER).build();
        }
        return organizationUserRepoPort.findByUserId(currentUserId)
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.INVALID_USER).build());
    }

    /**
     * Generate result filename with timestamp
     */
    protected String generateResultFileName() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "ket_qua_ban_goi_theo_lo_" + timestamp + ".xlsx";
    }

    /**
     * Create sale order for batch package sale (no lines yet).
     * Lines will be created for each successful registration.
     */
    protected SaleOrderDTO createSaleOrderForBatchPackage(String fileName, int totalNumber, Long totalAmount) {
        try {
            String currentUserId = SecurityUtil.getCurrentUserId();
            OrganizationUserDTO organizationUser = getCurrentOrganizationUserOrThrow(currentUserId);

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setOrgId(organizationUser.getOrgId());
            saleOrder.setOrderType(SaleOrderConstant.BATCH_PACKAGE);
            saleOrder.setAmountTotal(totalAmount);
            saleOrder.setQuantity((long) totalNumber);
            saleOrder.setOrderDate(LocalDateTime.now());
            saleOrder.setDescription("Batch package sale - " + fileName);
            saleOrder.setNote("Batch processing for file: " + fileName);

            // Save the sale order
            SaleOrderDTO savedSaleOrder = saleOrderRepoPort.saveAndFlush(saleOrder);

            log.info("Created sale order for batch package sale: Order ID={}", savedSaleOrder.getId());

            return savedSaleOrder;

        } catch (Exception e) {
            log.error("Error creating sale order for batch package sale: {}", fileName, e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi tạo đơn hàng cho batch package sale")
                .build();
        }
    }

    /**
     * Create batch package sale record
     */
    protected BatchPackageSaleDTO createBatchPackageSaleRecord(String fileName, String fileUrl, int totalNumber, Long totalAmount) {
        // Create sale order and sale order line first
        SaleOrderDTO saleOrder = createSaleOrderForBatchPackage(fileName, totalNumber, totalAmount);

        BatchPackageSaleDTO batchPackageSale = new BatchPackageSaleDTO();
        batchPackageSale.setFileName(fileName);
        batchPackageSale.setFileUrl(fileUrl);
        batchPackageSale.setTotalNumber((long) totalNumber);
        batchPackageSale.setFailedNumber(0L);
        batchPackageSale.setSucceededNumber(0L);
        batchPackageSale.setStatus(1); // Processing
        batchPackageSale.setType(2); // Batch sale
        batchPackageSale.setClientId(SecurityUtil.getCurrentClientId());
        batchPackageSale.setCreatedBy(SecurityUtil.getCurrentUsername());
        batchPackageSale.setCreatedDate(LocalDateTime.now());
        batchPackageSale.setOrderId(saleOrder.getId()); // Link to the created sale order

        return batchPackageSaleRepoPort.saveAndFlush(batchPackageSale);
    }

    /**
     * Update batch package sale with results
     */
    protected void updateBatchPackageSaleWithResults(BatchPackageSaleDTO batchPackageSale,
                                                     String resultFileUrl,
                                                     int succeededNumber,
                                                     int failedNumber) {
        batchPackageSale.setResultFileUrl(resultFileUrl);
        batchPackageSale.setSucceededNumber((long) succeededNumber);
        batchPackageSale.setFailedNumber((long) failedNumber);
        batchPackageSale.setStatus(2); // Completed
        batchPackageSale.setFinishedDate(LocalDateTime.now());

        batchPackageSaleRepoPort.save(batchPackageSale);
        log.info("Batch package sale updated with results: ID={}, Succeeded={}, Failed={}",
            batchPackageSale.getId(), succeededNumber, failedNumber);
    }

    /**
     * Generate result Excel file with processing results
     */
    protected byte[] generateResultExcelFile(List<ExcelSalePackage> excelData) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Kết quả xử lý");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Số điện thoại");
            headerRow.createCell(1).setCellValue("Mã gói cước");
            headerRow.createCell(2).setCellValue("Kết quả");
            headerRow.createCell(3).setCellValue("Thông báo lỗi");
            headerRow.createCell(4).setCellValue("Giá gói");

            // Add data rows
            int rowNum = 1;
            for (ExcelSalePackage data : excelData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getPhoneNumber());
                row.createCell(1).setCellValue(data.getPckCode());
                row.createCell(2).setCellValue(data.getResult() != null ? data.getResult() : "");
                row.createCell(3).setCellValue(data.getFailReason() != null ? data.getFailReason() : "");
                row.createCell(4).setCellValue(data.getPrice() != null ? data.getPrice() : 0);
            }

            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert to byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            log.error("Error generating result Excel file", e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi tạo file Excel kết quả")
                .build();
        }
    }

    protected void validateFileFormat(MultipartFile attachment) {
        String fileName = attachment.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw BaseException.badRequest(ErrorCode.INVALID_FILE_FORMAT)
                .build();
        }
    }

    protected void validateTemplateFormat(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw BaseException.badRequest(ErrorCode.INVALID_FILE_TEMPLATE)
                .build();
        }

        int actualColumnCount = headerRow.getLastCellNum();
        int expectedColumnCount = Constant.UploadFile.HEADER_FILE_SALE_PACKAGE.size();

        if (actualColumnCount != expectedColumnCount) {
            log.error("Template column count mismatch. Expected: {}, Actual: {}", expectedColumnCount, actualColumnCount);
            throw BaseException.badRequest(ErrorCode.INVALID_FILE_TEMPLATE)
                .build();
        }

        List<String> actualHeaders = new ArrayList<>();

        for (int i = 0; i < Constant.UploadFile.HEADER_FILE_SALE_PACKAGE.size(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = (cell != null) ? cell.toString().trim() : "";
            actualHeaders.add(headerValue);
        }

        for (int i = 0; i < Constant.UploadFile.HEADER_FILE_SALE_PACKAGE.size(); i++) {
            if (i >= actualHeaders.size() || !Constant.UploadFile.HEADER_FILE_SALE_PACKAGE.get(i).equals(actualHeaders.get(i))) {
                log.error("Template header mismatch at column {}. Expected: '{}', Actual: '{}'",
                    i + 1, Constant.UploadFile.HEADER_FILE_SALE_PACKAGE.get(i), i < actualHeaders.size() ? actualHeaders.get(i) : "N/A");
                throw BaseException.badRequest(ErrorCode.INVALID_FILE_TEMPLATE)
                    .build();
            }
        }

        log.debug("Template format validation passed. Column count: {}, Headers: {}", expectedColumnCount, Constant.UploadFile.HEADER_FILE_SALE_PACKAGE);
    }

    protected void validateFileNotEmpty(Sheet sheet) {
        boolean hasDataRow = false;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && !isRowEmpty(row)) {
                hasDataRow = true;
                break;
            }
        }

        if (!hasDataRow) {
            throw BaseException.badRequest(ErrorCode.EMPTY_FILE)
                .build();
        }
    }

    protected void validateFileRowCount(Sheet sheet) {
        int dataRowCount = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && !isRowEmpty(row)) {
                dataRowCount++;
            }
        }

        if (dataRowCount > MAX_LIMIT_ROW_COUNT) {
            throw BaseException.badRequest(ErrorCode.FILE_ROW_COUNT_EXCEEDED)
                .build();
        }
    }

    protected boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = cell.toString().trim();
                if (!cellValue.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
