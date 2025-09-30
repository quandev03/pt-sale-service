package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.BatchPackageSaleDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.BatchPackageSaleServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.BatchPackageSaleRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PackageReportResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchPackageSaleService implements BatchPackageSaleServicePort {

    private final BatchPackageSaleRepoPort batchPackageSaleRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;

    private static final String FILE_EXCEL_TITLE = "BÁO CÁO BÁN GÓI CHO THUÊ BAO";
    private static final String FILE_EXCEL_SHEET = "Báo cáo bán gói cho thuê bao";
    private static final String DON_LE = "Đơn lẻ";
    private static final String THEO_LO = "Theo lô";

    @Override
    public Page<BatchPackageSaleDTO> searchBatchPackageSales(
        String q,
        Integer saleType,
        Integer status,
        String fromDate,
        String toDate,
        Pageable pageable
    ) {
        String currentId = SecurityUtil.getCurrentUserId();
        String orgId = organizationUserRepoPort.findByUserId(currentId).orElseThrow(()->BaseException.badRequest(ErrorCode.ORG_ID_NOT_FOUND).build()).getOrgId();

        log.debug("Searching batch package sales with filters: fileNameOrSubscriberNumber={}, saleType={}, status={}, fromDate={}, toDate={}, orgId={}",
            q, saleType, status, fromDate, toDate, orgId);

        return batchPackageSaleRepoPort.searchBatchPackageSales(
            q, saleType, status, fromDate, toDate, orgId, pageable);
    }

    @Override
    public BatchPackageSaleDTO findById(String id) {
        log.debug("Finding batch package sale by ID: {}", id);

        return batchPackageSaleRepoPort.findById(id);
    }

    @Override
    public Page<PackageReportResponse> searchPackageReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        return batchPackageSaleRepoPort.searchPackageReport(currentOrgCode, request, pageable);
    }

    @Override
    public ByteArrayOutputStream exportPackageReport(String currentOrgCode, SearchRevenueReportRequest request) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(FILE_EXCEL_SHEET);
            List<PackageReportResponse> data =
                batchPackageSaleRepoPort.getPackageReport(currentOrgCode, request);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constant.TIME_STAMP_FE_DATE);

            // ======================== Fonts ========================
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font filterFont = workbook.createFont();
            filterFont.setBold(true);
            filterFont.setFontHeightInPoints((short) 14);

            // ======================== Styles ========================
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle filterStyle = workbook.createCellStyle();
            filterStyle.setFont(filterFont);
            filterStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle labelStyle = workbook.createCellStyle();
            labelStyle.setFont(boldFont);
            labelStyle.setAlignment(HorizontalAlignment.LEFT);

            Font whiteBoldFont = workbook.createFont();
            whiteBoldFont.setBold(true);
            whiteBoldFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(whiteBoldFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);
            textStyle.setBorderTop(BorderStyle.THIN);
            textStyle.setBorderLeft(BorderStyle.THIN);
            textStyle.setBorderRight(BorderStyle.THIN);
            textStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(textStyle);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(textStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ======================== Title ========================
            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(FILE_EXCEL_TITLE);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            Row filterRow = sheet.createRow(rowIdx++);
            Cell filterCell = filterRow.createCell(0);
            String filterTime = "Từ ngày: " + formatDateForDisplay(request.getStartDate()) + " - Đến ngày: " + formatDateForDisplay(request.getEndDate());
            filterCell.setCellValue(filterTime);
            filterCell.setCellStyle(filterStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 8));

            rowIdx++; // Dòng trống

            // ======================== Header ========================
            String[] headers = {
                "STT", "Tên file/Số thuê bao", "Mã đại lý", "Tên đại lý",
                "Gói cước", "Tổng tiền gói cước", "Hình thức bán gói", "Người thực hiện", "Ngày thực hiện"
            };

            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ======================== Data ========================
            int stt = 1;
            for (PackageReportResponse pack : data) {
                Row row = sheet.createRow(rowIdx++);
                int colIdx = 0;

                row.createCell(colIdx).setCellValue(stt++);
                row.getCell(colIdx++).setCellStyle(centerStyle);

                row.createCell(colIdx).setCellValue(pack.getFileUrl() != null ? pack.getFileUrl() : pack.getIsdn());
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(defaultStr(pack.getOrgCode()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(defaultStr(pack.getOrgName()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(defaultStr(pack.getPckCode()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(pack.getAmountTotal() != null ? pack.getAmountTotal().doubleValue() : 0);
                row.getCell(colIdx++).setCellStyle(numberStyle);

                row.createCell(colIdx).setCellValue(pack.getType() == 1 ? DON_LE : THEO_LO);
                row.getCell(colIdx++).setCellStyle(centerStyle);

                row.createCell(colIdx).setCellValue(defaultStr(pack.getCreatedBy()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(pack.getOrderDate() != null ? pack.getOrderDate().format(dateFormatter) : "");
                row.getCell(colIdx).setCellStyle(centerStyle);
            }

            // ======================== Set Column Widths ========================
            int[] columnWidths = {8, 20, 15, 55, 15, 20, 20, 30, 25};
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, columnWidths[i] * 256);
            }

            // bật wrapText cho các style data
            textStyle.setWrapText(true);
            numberStyle.setWrapText(true);
            centerStyle.setWrapText(true);

            workbook.write(outputStream);
            log.info("Xuất Excel thành công");
            return outputStream;

        } catch (Exception e) {
            log.error("Lỗi khi xuất Excel: {}", e.getMessage(), e);
            throw BaseException.bussinessError(ErrorCode.ERROR_EXPORT_EXCEL).build();
        }
    }

    private String formatDateForDisplay(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "";
        try {
            // Input: yyyy/MM/dd -> Output: dd/MM/yyyy
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
            log.warn("Error formatting date: {}", dateStr);
        }
        return dateStr;
    }

    private String defaultStr(String input) {
        return input != null ? input : "";
    }

}
