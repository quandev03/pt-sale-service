package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;

import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.*;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PartnerPackageService extends BasePackageServicePort {

    private static final Pattern PHONE_PATTERN = Constant.UploadFile.PHONE_PATTERN;

    public PartnerPackageService(PackageProfileRepoPort packageProfileRepoPort, SubscriberRepoPort subscriberRepoPort,
                                 BatchPackageSaleRepoPort batchPackageSaleRepoPort, SaleOrderRepoPort saleOrderRepoPort,
                                 SaleOrderLineRepoPort saleOrderLineRepoPort, OrganizationUserRepoPort organizationUserRepoPort,
                                 MinioOperations minioOperations, RestTemplate restTemplate, IntegrationPort integrationPort,
                                 KafkaProducerPort kafkaProducerPort, OrganizationUnitRepoPort organizationUnitRepoPort,
                                 ActionHistoryRepoPort actionHistoryRepoPort, OrganizationUnitServicePort organizationUnitServicePort) {
        super(packageProfileRepoPort, subscriberRepoPort, batchPackageSaleRepoPort, saleOrderRepoPort, saleOrderLineRepoPort,
            organizationUserRepoPort, minioOperations, restTemplate, integrationPort, kafkaProducerPort, organizationUnitRepoPort, actionHistoryRepoPort, organizationUnitServicePort);
    }

    @Override
    public Object registerPackage(SalePackageDTO salePackage) {
        log.info("Partner package service - registering package for ISDN: {}", salePackage.getIsdn());

        // Validate phone/isdn before handling
        if (!StringUtils.hasText(salePackage.getIsdn()) || !PHONE_PATTERN.matcher(salePackage.getIsdn()).matches()) {
            throw BaseException.badRequest(ErrorCode.ISDN_INVALID)
                .build();
        }

        String isdn = convertPhoneToIsdn(salePackage.getIsdn());

        if (!checkIsdn(isdn)) {
            throw BaseException.notFoundError(ErrorCode.ISDN_NOT_FOUND)
                .build();
        }

        Set<String> authorizedPcks = getAuthorizedPckCode();
        if (!authorizedPcks.contains(salePackage.getPckCode())) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_NOT_AUTHORIZED)
                .build();
        }

        Long price;
        price = getPackagePriceOrThrowIfInvalid(salePackage.getPckCode());

        checkLimit(salePackage.getPckCode(), price);

        // Tạo đơn hàng trước
        SaleOrderDTO saleOrder = createSaleOrderForSinglePackage(salePackage.getIsdn(), salePackage.getPckCode(), price);

        // Đăng ký gói, chỉ tạo dòng đơn hàng nếu đăng ký thành công
        processPackageRegistration(salePackage);

        // Nếu thành công tới đây thì tạo dòng đơn hàng (tách try/catch sang phương thức riêng để giảm complexity)
        createSaleOrderLineForSingleRegistration(saleOrder.getId(), salePackage, price);

        updateSubscriberPackCode(isdn, salePackage.getPckCode());

        // Cập nhật công nợ tạm tính + công nợ thực tế
        this.updateDebitLimit(price);

        try {
            BatchPackageSaleDTO singleRecord = BatchPackageSaleDTO.builder()
                .fileUrl(null)
                .resultFileUrl(null)
                .totalNumber(1L)
                .failedNumber(0L)
                .succeededNumber(1L)
                .paymentType(null)
                .status(2) // completed
                .fileName(null)
                .clientId(com.vnsky.security.SecurityUtil.getCurrentClientId())
                .type(1) // single
                .orderId(saleOrder.getId())
                .createdBy(com.vnsky.security.SecurityUtil.getCurrentUsername())
                .createdDate(java.time.LocalDateTime.now())
                .finishedDate(java.time.LocalDateTime.now())
                .build();
            batchPackageSaleRepoPort.saveAndFlush(singleRecord);
        } catch (Exception ex) {
            log.warn("Failed to create batch package sale record for single registration: {}", ex.getMessage());
        }

        return "Đăng ký gói thành công";
    }

    @Override
    public List<ExcelSalePackage> checkData(MultipartFile attachment) {
        log.info("Partner package service - checking data from file: {}", attachment.getOriginalFilename());

        if (attachment.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.EMPTY_FILE)
                .build();
        }

        // Validate file format
        validateFileFormat(attachment);

        try {
            List<ExcelSalePackage> result = new ArrayList<>();

            try (Workbook workbook = new XSSFWorkbook(attachment.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                // Validate file is not empty (has data rows)
                validateFileNotEmpty(sheet);

                // Validate template format
                validateTemplateFormat(sheet);

                // Validate file size (row count)
                validateFileRowCount(sheet);

                // Skip header row, start from row 1
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row)) {
                        continue;
                    }

                    ExcelSalePackage excelData = parseRowToExcelSalePackage(row, i + 1);
                    validateExcelRowData(excelData);
                    result.add(excelData);
                }
            }

            // Check total price and limit for successful rows
            if (result.stream().anyMatch(e -> Constant.UploadFile.SalePackageBatch.SUCCESS.equals(e.getResult()))) {
                long totalPrice = result.stream()
                    .filter(e -> Constant.UploadFile.SalePackageBatch.SUCCESS.equals(e.getResult()))
                    .mapToLong(ExcelSalePackage::getPrice)
                    .sum();

                // Check if total price exceeds organization limit
                checkLimitExcel(totalPrice);
            }

            log.info("Processed {} rows from Excel file", result.size());
            return result;

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", attachment.getOriginalFilename(), e);
            throw BaseException.badRequest(ErrorCode.INVALID_FILE_FORMAT)
                .build();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing Excel file: {}", attachment.getOriginalFilename(), e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @Override
    @Transactional
    public Object submitData(MultipartFile attachment) {
        log.info("Partner package service - submitting data from file: {} with PIN confirmation", attachment.getOriginalFilename());

        // Process Excel file first to validate data
        List<ExcelSalePackage> excelData = checkData(attachment);

        // Calculate total amount for successful rows (use for limit and creating record)
        long totalAmount = excelData.stream()
            .filter(e -> Constant.UploadFile.SalePackageBatch.SUCCESS.equals(e.getResult()))
            .mapToLong(ExcelSalePackage::getPrice)
            .sum();

        // Create batch package sale record at the start
        BatchPackageSaleDTO batchPackageSale = createBatchPackageSaleRecord(
            attachment.getOriginalFilename(),
            uploadFileToMinio(attachment, attachment.getOriginalFilename()),
            excelData.size(),
            totalAmount
        );

        // Check limit before processing
        checkLimitExcel(totalAmount);

        // Process batch package registration, create lines into existing sale order
        processBatchPackageSubmission(excelData, batchPackageSale.getOrderId());

        // Generate result Excel file with processing results
        byte[] resultExcelBytes = generateResultExcelFile(excelData);

        // Upload result file to MinIO with timestamp
        String resultFileName = generateResultFileName();
        String resultFileUrl = uploadFileToMinio(resultExcelBytes, resultFileName);

        // Count success and failure
        int succeededCount = (int) excelData.stream()
            .filter(e -> Constant.UploadFile.SalePackageBatch.SUCCESS.equals(e.getResult()))
            .count();
        int failedCount = excelData.size() - succeededCount;

        // Update batch package sale with results
        updateBatchPackageSaleWithResults(batchPackageSale, resultFileUrl, succeededCount, failedCount);

        String result = String.format("Xử lý hoàn tất: %d thành công, %d thất bại. File kết quả: %s",
            succeededCount, failedCount, resultFileName);
        log.info("Batch submission completed: {} success, {} failed", succeededCount, failedCount);

        // Reduce debit by total price of successful rows
        long debitAmount = excelData.stream()
            .filter(e -> Constant.UploadFile.SalePackageBatch.SUCCESS.equals(e.getResult()))
            .mapToLong(ExcelSalePackage::getPrice)
            .sum();
        this.updateDebitLimit(debitAmount);

        return result;
    }

    private void processBatchPackageSubmission(List<ExcelSalePackage> excelData, String saleOrderId) {
        log.info("Processing batch package submission for {} rows", excelData.size());

        int successCount = 0;
        int failCount = 0;

        for (ExcelSalePackage data : excelData) {
            boolean isRowEligible = Constant.UploadFile.SalePackageBatch.SUCCESS.equals(data.getResult());

            if (!isRowEligible) {
                failCount++;
                continue; // only one continue in the loop
            }

            try {
                // Đăng ký gói cho từng dòng hợp lệ
                processBatchPackageRegistration(data);

                // Nếu đăng ký đặt lỗi (đã set failReason) thì đánh fail
                if (hasRegistrationFailed(data)) {
                    markRowAsFailed(data, null);
                    failCount++;
                } else {
                    // Tạo dòng đơn hàng an toàn (được tách ra phương thức riêng)
                    boolean created = createSaleOrderLineForBatchRow(saleOrderId, data);
                    if (created) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
            } catch (Exception e) {
                log.error("Error processing row for phone: {}", data.getPhoneNumber(), e);
                markRowAsFailed(data, "Lỗi xử lý: " + e.getMessage());
                failCount++;
            }
        }

        log.info("Batch submission processing completed: {} success, {} failed", successCount, failCount);
    }

    private void createSaleOrderLineForSingleRegistration(String saleOrderId, SalePackageDTO salePackage, Long price) {
        try {
            createSaleOrderLine(
                saleOrderId,
                convertPhoneToIsdn(salePackage.getIsdn()),
                salePackage.getPckCode(),
                price
            );
        } catch (BaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Không thể tạo dòng đơn hàng cho đăng ký đơn lẻ: orderId={}, isdn={}, pck={}",
                saleOrderId, salePackage.getIsdn(), salePackage.getPckCode(), ex);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Lỗi tạo dòng đơn hàng")
                .build();
        }
    }

    private boolean hasRegistrationFailed(ExcelSalePackage data) {
        return data.getFailReason() != null && !data.getFailReason().isEmpty();
    }

    private void markRowAsFailed(ExcelSalePackage data, String failMessage) {
        if (failMessage != null && !failMessage.isEmpty()) {
            data.setFailResult(failMessage);
        }
        data.setResult(Constant.UploadFile.SalePackageBatch.FAIL);
    }

    private boolean createSaleOrderLineForBatchRow(String saleOrderId, ExcelSalePackage data) {
        try {
            createSaleOrderLine(
                saleOrderId,
                convertPhoneToIsdn(data.getPhoneNumber()),
                data.getPckCode(),
                data.getPrice()
            );
            return true;
        } catch (BaseException ex) {
            markRowAsFailed(data, "Lỗi tạo dòng đơn hàng");
            return false;
        } catch (Exception ex) {
            markRowAsFailed(data, "Lỗi tạo dòng đơn hàng: " + ex.getMessage());
            return false;
        }
    }

    private void processPackageRegistration(SalePackageDTO salePackage) {
        log.debug("Processing package registration via external API for ISDN: {}", salePackage.getIsdn());
        try {
            // Convert phone number to ISDN by removing leading zeros
            String isdn = convertPhoneToIsdn(salePackage.getIsdn());

            registerPackageByIsdn(isdn, salePackage.getPckCode());
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error registering package for ISDN: {}", salePackage.getIsdn(), e);
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .build();
        }
    }


    private ExcelSalePackage parseRowToExcelSalePackage(Row row, int rowNumber) {
        ExcelSalePackage data = new ExcelSalePackage();

        try {
            // Column 0: Phone Number
            Cell phoneCell = row.getCell(0);
            if (phoneCell != null) {
                data.setPhoneNumber(getCellValueAsString(phoneCell));
            }

            // Column 1: Package Code (no more serial column)
            Cell packageCell = row.getCell(1);
            if (packageCell != null) {
                data.setPckCode(getCellValueAsString(packageCell));
            }

        } catch (Exception e) {
            log.error("Error parsing row {}: {}", rowNumber, e.getMessage());
            data.setFailResult("Lỗi đọc dữ liệu dòng " + rowNumber);
        }

        return data;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private void validateExcelRowData(ExcelSalePackage data) {
        StringBuilder resultBuilder = new StringBuilder();

        // Validate required fields
        validateDataEmpty(resultBuilder, data);
        if (!resultBuilder.isEmpty()) {
            data.setFailResult(resultBuilder.toString());
            return;
        }

        // Validate phone number format
        if (!PHONE_PATTERN.matcher(data.getPhoneNumber()).matches()) {
            resultBuilder.append(Constant.UploadFile.INVALID_PHONE_NUMBER);
        }

        // Validate package code
        validatePackage(resultBuilder, data);

        // Check authorized package codes
        Set<String> authorizedPcks = getAuthorizedPckCode();
        if (!authorizedPcks.contains(data.getPckCode())) {
            resultBuilder.append("Gói cước không được phân quyền cho đối tác; ");
        }

        // Validate against database
        validateDataDb(resultBuilder, data);

        if (!resultBuilder.isEmpty()) {
            data.setFailResult(resultBuilder.toString());
        } else {
            data.setResult(Constant.UploadFile.SalePackageBatch.SUCCESS);
        }
    }

    private void validateDataEmpty(StringBuilder resultBuilder, ExcelSalePackage data) {
        if (!StringUtils.hasText(data.getPhoneNumber())) {
            resultBuilder.append(Constant.UploadFile.PHONE_NUMBER_NOT_FOUND);
        }
        if (!StringUtils.hasText(data.getPckCode())) {
            resultBuilder.append(Constant.UploadFile.PACKAGE_CODE_NOT_FOUND);
        }
    }

    private void validatePackage(StringBuilder resultBuilder, ExcelSalePackage data) {
        // Check if package exists and get price
        if (!packageProfileRepoPort.isExistPackageCode(data.getPckCode())) {
            resultBuilder.append(Constant.UploadFile.PACKAGE_NOT_FOUND);
        } else {
            try {
                Long price = getPackagePriceOrThrowIfInvalid(data.getPckCode());
                data.setPrice(price);
                log.debug("Retrieved package price: {} for package: {}", price, data.getPckCode());
            } catch (BaseException ex) {
                // price invalid (<=0) or other domain error
                resultBuilder.append("Giá gói cước phải lớn hơn 0; ");
                data.setPrice(0L);
            } catch (Exception e) {
                log.error("Could not retrieve package price for: {}", data.getPckCode(), e);
                data.setPrice(0L);
            }
        }
    }

    private void validateDataDb(StringBuilder resultBuilder, ExcelSalePackage data) {
        try {
            // Convert phone number to ISDN by removing leading zeros
            String isdn = convertPhoneToIsdn(data.getPhoneNumber());

            // Validate subscriber exists using converted ISDN
            boolean subscriberExists = checkIsdn(isdn);
            if (!subscriberExists) {
                resultBuilder.append("Thuê bao không tồn tại; ");
            }
        } catch (Exception e) {
            log.error("Error validating subscriber data for: {}", data.getPhoneNumber(), e);
            resultBuilder.append("Lỗi kiểm tra thông tin thuê bao; ");
        }
    }

    private void processBatchPackageRegistration(ExcelSalePackage data) {
        log.debug("Processing batch package registration via external API for phone: {}, package: {}",
            data.getPhoneNumber(), data.getPckCode());
        try {
            // Convert phone number to ISDN by removing leading zeros
            String isdn = convertPhoneToIsdn(data.getPhoneNumber());

            registerPackageByIsdn(isdn, data.getPckCode());

            updateSubscriberPackCode(isdn, data.getPckCode());
            log.info("Batch package registration successful for phone: {} (ISDN: {}), package: {}",
                data.getPhoneNumber(), isdn, data.getPckCode());

        } catch (BaseException e) {
            // Preserve the original error message from BaseException
            String errorMessage = e.getMessage();
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                data.setFailResult(errorMessage);
            } else {
                data.setFailResult("Lỗi đăng ký gói cước");
            }
            log.error("Error in batch package registration for phone: {}: {}", data.getPhoneNumber(), errorMessage);
        } catch (Exception e) {
            // For other unexpected exceptions, use generic error message
            log.error("Unexpected error in batch package registration for phone: {}", data.getPhoneNumber(), e);
            data.setFailResult("Lỗi xử lý: " + e.getMessage());
        }
    }

    private void updateDebitLimit(Long price) {
        OrganizationUnitDTO organizationUnitDTO = organizationUnitServicePort.getOrgCurrent();
        organizationUnitDTO.setDebtLimit(organizationUnitDTO.getDebtLimit() - price);
        organizationUnitDTO.setDebtLimitMbf(organizationUnitDTO.getDebtLimitMbf() - price);
        organizationUnitRepoPort.save(organizationUnitDTO);
    }
}
