package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.ExcelSalePackage;
import com.vnsky.bcss.projectbase.domain.dto.SalePackageDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.SalePackageServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.BatchPackageSaleServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.SalePackageOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SalePackageRest implements SalePackageOperation {

    private final SalePackageServicePort salePackageServicePort;
    private final BatchPackageSaleServicePort batchPackageSaleServicePort;

    @Override
    public ResponseEntity<Object> checkIsdn(String isdn, Integer type) {
        return ResponseEntity.ok(salePackageServicePort.checkIsdn(isdn, type));
    }

    @Override
    public ResponseEntity<Object> registerPackage(SalePackageDTO salePackage) {
        return ResponseEntity.ok(salePackageServicePort.registerPackage(salePackage));
    }

    @Override
    public ResponseEntity<Object> checkData(MultipartFile attachment) {
        log.info("Checking data from file: {}", attachment.getOriginalFilename());

        List<ExcelSalePackage> response = salePackageServicePort.checkData(attachment);

        // Check if there are any failed rows
        boolean hasErrors = response.stream().anyMatch(e ->
            Objects.nonNull(e.getResult()) &&
            !Objects.equals(e.getResult(), Constant.UploadFile.SalePackageBatch.SUCCESS)
        );

        if (hasErrors) {
            // Return Excel file with error details
            byte[] excelBytes = generateErrorExcelFile(response);
            return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment().filename("Ban-goi-theo-lo-loi.xlsx").build().toString())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
        }

        // All rows are valid, return success response with OTP request info
        return ResponseEntity.ok()
            .body("Dữ liệu hợp lệ. Vui lòng xác thực OTP để tiếp tục xử lý " + response.size() + " bản ghi.");
    }

    @Override
    public ResponseEntity<Object> submitData(MultipartFile attachment) {
        return ResponseEntity.ok(salePackageServicePort.submitData(attachment));
    }

    @Override
    public ResponseEntity<Object> getSampleCreateTicketOut() {
        log.info("Generating Excel template for sale package batch");

        try {
            byte[] excelBytes = generateSampleExcelFile();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment().filename("danh-sach-thue-bao-nap-goi-mau.xlsx").build().toString())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);

        } catch (Exception e) {
            log.error("Error generating Excel template", e);
            throw BaseException.internalServerError(ErrorCode.ERROR_EXPORT_EXCEL)
                .message("Lỗi tạo file Excel mẫu")
                .build();
        }
    }

    @Override
    public ResponseEntity<Object> searchBatchPackageSales(
        String q,
        Integer saleType,
        Integer status,
        String fromDate,
        String toDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(batchPackageSaleServicePort.searchBatchPackageSales(q, saleType, status, fromDate, toDate, pageable));
    }

    // Helper methods for Excel processing

    private byte[] generateErrorExcelFile(List<ExcelSalePackage> data) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Kết quả kiểm tra");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Số thuê bao");
            headerRow.createCell(1).setCellValue("Serial SIM");
            headerRow.createCell(2).setCellValue("Mã gói cước");
            headerRow.createCell(3).setCellValue("Kết quả");
            headerRow.createCell(4).setCellValue("Lý do thất bại");

            // Create data rows
            for (int i = 0; i < data.size(); i++) {
                ExcelSalePackage item = data.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(item.getPhoneNumber() != null ? item.getPhoneNumber() : "");
                row.createCell(2).setCellValue(item.getPckCode() != null ? item.getPckCode() : "");
                row.createCell(3).setCellValue(item.getResult() != null ? item.getResult() : "");
                row.createCell(4).setCellValue(item.getFailReason() != null ? item.getFailReason() : "");
            }

            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generating error Excel file", e);
            throw BaseException.internalServerError(ErrorCode.ERROR_EXPORT_EXCEL)
                .message("Lỗi tạo file Excel báo lỗi")
                .build();
        }
    }

    private byte[] generateSampleExcelFile() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Mẫu dữ liệu");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Số thuê bao");
            headerRow.createCell(1).setCellValue("Mã gói cước");

            // Create sample data rows
            Row sampleRow1 = sheet.createRow(1);
            sampleRow1.createCell(0).setCellValue("0987654321");
            sampleRow1.createCell(1).setCellValue("HVN0");

            Row sampleRow2 = sheet.createRow(2);
            sampleRow2.createCell(0).setCellValue("0123456789");
            sampleRow2.createCell(1).setCellValue("HVN90");

            // Auto-size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generating sample Excel file", e);
            throw BaseException.internalServerError(ErrorCode.ERROR_EXPORT_EXCEL)
                .message("Lỗi tạo file Excel mẫu")
                .build();
        }
    }
}
