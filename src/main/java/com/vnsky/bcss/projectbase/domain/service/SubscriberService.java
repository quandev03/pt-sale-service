package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberStatusResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.StockIsdnStatus;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService implements SubscriberServicePort {
    private final SubscriberRepoPort subscriberRepoPort;
    private final MinioOperations minioOperations;

    private static final String TITLE_FILE_REPORT = "BÁO CÁO CẬP NHẬT THÔNG TIN THUÊ BAO";
    private static final String SHEET_NAME_FILE_REPORT = "Báo cáo cập nhật thông tin thuê bao";
    private static final String TITLE_FILE_SUBS = "DANH SÁCH THUÊ BAO";
    private static final String SHEET_NAME_FILE_SUBS = "Danh sách thuê bao";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public SubscriberDTO saveAndFlushNewTransaction(SubscriberDTO dto) {
        return subscriberRepoPort.saveAndFlush(dto);
    }

    @Override
    public SubscriberDTO findByIsdn(Long isdn) {
        return subscriberRepoPort.findByLastIsdn(isdn).orElse(null);
    }

    @Override
    public Resource downloadFile(String url) {
        DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
            .uri(url)
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOption);
    }

    @Override
    public Page<SearchSubscriberResponse> searchSubscriber(String q, Integer status, String orgCode, Pageable page) {
        return subscriberRepoPort.searchSubscriber(q, status, orgCode, page);
    }

    @Override
    public Page<SubscriberReportResponse> searchSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        return subscriberRepoPort.searchSubscriberReport(currentOrgCode, request, pageable);
    }

    @Override
    public ByteArrayOutputStream exportSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME_FILE_REPORT);
            List<SubscriberReportResponse> data =
                subscriberRepoPort.getSubscriberReport(currentOrgCode, request);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN);

            // Fonts & Styles
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font filterFont = workbook.createFont();
            filterFont.setBold(true);
            filterFont.setFontHeightInPoints((short) 14);

            Font whiteBoldFont = workbook.createFont();
            whiteBoldFont.setBold(true);
            whiteBoldFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle filterStyle = workbook.createCellStyle();
            filterStyle.setFont(filterFont);
            filterStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle labelStyle = workbook.createCellStyle();
            labelStyle.setFont(boldFont);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(whiteBoldFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setWrapText(true);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);
            textStyle.setBorderTop(BorderStyle.THIN);
            textStyle.setBorderLeft(BorderStyle.THIN);
            textStyle.setBorderRight(BorderStyle.THIN);
            textStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(textStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title
            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(TITLE_FILE_REPORT);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

            Row filterRow = sheet.createRow(rowIdx++);
            Cell filterCell = filterRow.createCell(0);
            String filterTime = "Từ ngày: " + formatDateForDisplay(request.getStartDate()) + " - Đến ngày: " + formatDateForDisplay(request.getEndDate());
            filterCell.setCellValue(filterTime);
            filterCell.setCellStyle(filterStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 14));

            rowIdx++; // dòng trống

            // Header
            String[] headers = {
                "STT", "Mã hợp đồng", "Mã khách hàng", "Tên khách hàng", "Số thuê bao", "Serial SIM", "Gói cước",
                "Số hộ chiếu", "Trạng thái chặn cắt", "Quốc tịch", "Giới tính", "Ngày sinh", "Đại lý", "Người CNTTTB", "Thời gian cập nhật"
            };

            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int stt = 1;
            for (SubscriberReportResponse sub : data) {
                Row row = sheet.createRow(rowIdx++);
                int c = 0;

                row.createCell(c).setCellValue(stt++);
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getContractCode()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getCustomerCode()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getFullName()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(sub.getIsdn() != null ? sub.getIsdn().toString() : "");
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getSerial()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getPackCode()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getIdNumber()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(changeActiveStatus(sub.getActiveStatus()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getNationality()));
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(sub.getGender() != null ? (sub.getGender() == 1 ? "Nam" : "Nữ") : "");
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(
                    sub.getDateOfBirth() != null ? sub.getDateOfBirth().format(dateFormatter) : ""
                );
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getOrgName()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getUpdateInfoBy()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(
                    sub.getUpdateInfoDate() != null ? sub.getUpdateInfoDate().format(DateTimeFormatter.ofPattern(Constant.TIME_STAMP_FE_DATE)) : ""
                );
                row.getCell(c).setCellStyle(centerStyle);
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Xuất Excel thành công");
            return outputStream;

        } catch (Exception e) {
            log.error("Lỗi khi xuất Excel: {}", e.getMessage(), e);
            throw BaseException.bussinessError(ErrorCode.ERROR_EXPORT_EXCEL).build();
        }
    }

    @Override
    public ByteArrayOutputStream exportSubscriber(String q, Integer status, String orgCode) {
        log.info("Bắt đầu xuất Excel cho Danh sách số với điều kiện: Mã đại lý = {}, Từ khóa = {}", orgCode, q);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME_FILE_SUBS);
            List<SearchSubscriberResponse> data = subscriberRepoPort.getSubscriber(q, status, orgCode);

            // Fonts & Styles
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font whiteBoldFont = workbook.createFont();
            whiteBoldFont.setBold(true);
            whiteBoldFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(whiteBoldFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setWrapText(true);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.cloneStyleFrom(centerStyle);
            textStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(textStyle);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Title
            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(TITLE_FILE_SUBS);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            rowIdx++; // Dòng trống

            // Header
            String[] headers = {"STT", "Số", "Đại lý", "Trạng thái"};

            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int stt = 1;
            for (SearchSubscriberResponse sub : data) {
                Row row = sheet.createRow(rowIdx++);
                int c = 0;

                row.createCell(c).setCellValue(stt++);
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(sub.getIsdn() != null ? sub.getIsdn().toString() : "");
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getOrgName()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(sub.getStatusText());
                row.getCell(c).setCellStyle(centerStyle);
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

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

    @Override
    public List<SubscriberStatusResponse> getAllStatus() {
        return StockIsdnStatus.getStatusList();
    }

    private String defaultStr(String input) {
        return input != null ? input : "";
    }

    private String changeActiveStatus(Integer activeStatus) {
        if (activeStatus == null) return "";

        return switch (activeStatus) {
            case 1  -> Constant.SubscriberStatusConstant.KHONG_BI_CHAN;
            case 10 -> Constant.SubscriberStatusConstant.CHAN_MOT_CHIEU_YEU_CAU;
            case 20 -> Constant.SubscriberStatusConstant.CHAN_HAI_CHIEU_YEU_CAU;
            case 11 -> Constant.SubscriberStatusConstant.CHAN_MOT_CHIEU_NHA_MANG;
            default -> Constant.SubscriberStatusConstant.CHAN_HAI_CHIEU_NHA_MANG;
        };
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
}
