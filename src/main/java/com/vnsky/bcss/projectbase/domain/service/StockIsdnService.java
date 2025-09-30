package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.port.primary.StockIsdnServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchStockIsdnResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.StockIsdnStatus;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockIsdnService implements StockIsdnServicePort {
    private final StockIsdnRepoPort stockIsdnRepoPort;

    private static final String SHEET_NAME_FILE_ISDN = "Danh sách số";
        private static final String TITLE_FILE_ISDN = "DANH SÁCH SỐ";

    @Override
    public Page<SearchStockIsdnResponse> search(String q, Integer status, String orgCode, Pageable page) {
        return stockIsdnRepoPort.search(q, status, orgCode, page);
    }

    @Override
    public ByteArrayOutputStream exportSubscriber(String q, Integer status, String orgCode) {
        log.info("Bắt đầu xuất Excel cho Danh sách số với điều kiện: Mã đại lý = {}, Từ khóa = {}", orgCode, q);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME_FILE_ISDN);
            List<SearchStockIsdnResponse> data = stockIsdnRepoPort.getSubscriber(q, status, orgCode);

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
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
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

            // Title
            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(TITLE_FILE_ISDN);
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
            for (SearchStockIsdnResponse sub : data) {
                Row row = sheet.createRow(rowIdx++);
                int c = 0;

                row.createCell(c).setCellValue(stt++);
                row.getCell(c++).setCellStyle(centerStyle);

                row.createCell(c).setCellValue(sub.getIsdn() != null ? sub.getIsdn().toString() : "");
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(defaultStr(sub.getOrgName()));
                row.getCell(c++).setCellStyle(textStyle);

                row.createCell(c).setCellValue(StockIsdnStatus.fromValue(sub.getStatus()).getDescription());
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
    public Long totalEsimProcured(String orgCode) {
        return stockIsdnRepoPort.totalEsim(StockIsdnStatus.IN_STOCK.getValue(), orgCode);
    }

    @Override
    public Long totalEsimSold(String orgCode) {
        return stockIsdnRepoPort.totalEsim(StockIsdnStatus.SOLD.getValue(), orgCode);
    }

    @Override
    public Long totalSTBCalled900(String orgCode) {
        return stockIsdnRepoPort.totalEsim(StockIsdnStatus.CALLED_900.getValue(), orgCode);
    }

    @Override
    public Long revenusSTBCalled900(String orgCode) {
        return stockIsdnRepoPort.revenusEsimCalled900(StockIsdnStatus.CALLED_900.getValue(), orgCode);
    }

    private String defaultStr(String input) {
        return input != null ? input : "";
    }

}
