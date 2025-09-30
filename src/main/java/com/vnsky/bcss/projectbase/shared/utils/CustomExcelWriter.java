package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportDTO;
import com.vnsky.common.exception.domain.BaseException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Custom Excel Writer cho Summary Reports với format đặc biệt
 */
@UtilityClass
@Slf4j
public class CustomExcelWriter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");

    // Constants for header text
    private static final String DAY_PREFIX = "Ngày ";
    private static final String MONTH_PREFIX = "LK tháng ";
    private static final String YEAR_PREFIX = "LK năm ";
    private static final String COMPARISON_TEXT = "So với tháng trước (%)";
    private static final String TOTAL_ROW_TEXT = "TỔNG CỘNG";

    /**
     * Tạo Excel cho Summary All Report với format 2 cấp header
     */
    public static byte[] createSummaryAllExcel(SummaryAllReportDTO data, String startDate, String endDate) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet("Báo cáo tổng hợp kết quả");

            // Tạo styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            // Style cho cột STT (căn giữa + viền như data)
            CellStyle sttStyle = workbook.createCellStyle();
            sttStyle.cloneStyleFrom(dataStyle);
            sttStyle.setAlignment(HorizontalAlignment.CENTER);
            sttStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(sttStyle);

            int rowIndex = 0;

            // Title
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO TỔNG HỢP KẾT QUẢ");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

            // Header với thông tin ngày (từ ngày - đến ngày)
            Row headerRow = sheet.createRow(rowIndex++);
            Cell headerCell = headerRow.createCell(0);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            headerCell.setCellValue(String.format("Từ ngày: %s - Đến ngày: %s", start.format(DATE_FORMATTER), end.format(DATE_FORMATTER)));
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));

            // Main header row
            Row mainHeaderRow = sheet.createRow(rowIndex++);
            createSummaryAllMainHeaders(mainHeaderRow, headerStyle);

            // Sub header row
            Row subHeaderRow = sheet.createRow(rowIndex++);
            createSummaryAllSubHeaders(subHeaderRow, subHeaderStyle, startDate, endDate);

            // Add merge regions for main headers
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0)); // STT
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1)); // Nguồn
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 5)); // Số lượng Đại lý phát sinh giao dịch
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 9)); // Số Lượng Thuê Bao Đặt Hàng
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 10, 13)); // Số lượng Thuê Bao Mua Gói

            // Auto-size STT column based on header/sub-header (before data is written)
            sheet.trackColumnForAutoSizing(0);
            sheet.autoSizeColumn(0);
            int sttWidth = sheet.getColumnWidth(0);

            // Data row
            Row dataRow = sheet.createRow(rowIndex++);
            createSummaryAllDataRow(dataRow, data, dataStyle, numberStyle);
            // Áp style cho ô STT (cột 0)
            Cell sttCell = dataRow.getCell(0);
            if (sttCell != null) {
                sttCell.setCellStyle(sttStyle);
            }

            // Auto-size columns for data (skip STT to keep header-based width)
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < 14; i++) {
                sheet.autoSizeColumn(i);
            }
            // Restore STT width computed from header/sub-header
            sheet.setColumnWidth(0, sttWidth);

            // Đảm bảo tất cả các cell đều có borders (0..13)
            createTableBorders(sheet, rowIndex - 1, 13);

            return writeToByteArray(workbook);
        } catch (Exception e) {
            log.error("Lỗi khi tạo Excel Summary All: {}", e.getMessage(), e);
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    /**
     * Tạo Excel cho Summary By Org Report với format 2 cấp header
     */
    public static byte[] createSummaryByOrgExcel(List<SummaryByOrgReportDTO> data, String startDate, String endDate) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet("Báo cáo kết quả thuê bao - gói cước");

            // Tạo styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            // Style cho cột STT (căn giữa + viền như data)
            CellStyle sttStyle = workbook.createCellStyle();
            sttStyle.cloneStyleFrom(dataStyle);
            sttStyle.setAlignment(HorizontalAlignment.CENTER);
            sttStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(sttStyle);

            int rowIndex = 0;

            // Title
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO KẾT QUẢ THUÊ BAO - GÓI CƯỚC");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

            // Header với thông tin ngày
            Row headerRow = sheet.createRow(rowIndex++);
            Cell headerCell = headerRow.createCell(0);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            headerCell.setCellValue(String.format("Từ ngày: %s - Đến ngày: %s", start.format(DATE_FORMATTER), end.format(DATE_FORMATTER)));
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 14));

            // Main header row
            Row mainHeaderRow = sheet.createRow(rowIndex++);
            createSummaryByOrgMainHeaders(mainHeaderRow, headerStyle);

            // Sub header row
            Row subHeaderRow = sheet.createRow(rowIndex++);
            createSummaryByOrgSubHeaders(subHeaderRow, subHeaderStyle, startDate, endDate);

            // Add merge regions for main headers
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0)); // STT
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1)); // Công ty khu vực
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2)); // Tên Đại lý
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, 6)); // Số lượng thuê bao đặt hàng
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 10)); // Số lượng Thuê Bao kích hoạt 900
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 11, 14)); // Doanh thu gói cước

            // Auto-size STT column based on header/sub-header (before data is written)
            sheet.trackColumnForAutoSizing(0);
            sheet.autoSizeColumn(0);
            int sttWidth = sheet.getColumnWidth(0);

            // Auto-size columns for data (skip STT to keep header-based width)
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < 15; i++) {
                sheet.autoSizeColumn(i);
            }
            // Restore STT width computed from header/sub-header
            sheet.setColumnWidth(0, sttWidth);

            // Data rows
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(rowIndex++);
                SummaryByOrgReportDTO report = data.get(i);
                boolean isTotalRow = TOTAL_ROW_TEXT.equals(report.getOrgName());
                createSummaryByOrgDataRow(dataRow, report, isTotalRow ? -1 : i + 1, dataStyle, numberStyle, currencyStyle);
                // Áp style cho ô STT (cột 0)
                Cell sttCellOrg = dataRow.getCell(0);
                if (sttCellOrg != null) {
                    sttCellOrg.setCellStyle(sttStyle);
                }
            }

            // Thêm dòng tổng cộng nếu chưa có
            if (data.isEmpty() || !TOTAL_ROW_TEXT.equals(data.get(data.size() - 1).getOrgName())) {
                Row totalRow = sheet.createRow(rowIndex++);
                SummaryByOrgReportDTO totalData = calculateTotalRow(data);
                createSummaryByOrgDataRow(totalRow, totalData, -1, dataStyle, numberStyle, currencyStyle);
                // Style cho ô STT dòng tổng (trống nhưng vẫn đảm bảo viền/căn giữa)
                Cell sttTotal = totalRow.getCell(0);
                if (sttTotal != null) {
                    sttTotal.setCellStyle(sttStyle);
                }
            }

            // Auto-size columns - track all columns first for SXSSFWorkbook
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < 15; i++) {
                sheet.autoSizeColumn(i);
            }

            // Restore STT width computed from header/sub-header
            sheet.setColumnWidth(0, sttWidth);

            // Đảm bảo tất cả các cell đều có borders
            createTableBorders(sheet, rowIndex - 1, 14);

            return writeToByteArray(workbook);
        } catch (Exception e) {
            log.error("Lỗi khi tạo Excel Summary By Org: {}", e.getMessage(), e);
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    private static void createSummaryAllMainHeaders(Row row, CellStyle style) {
        // STT
        createCell(row, 0, "STT", style);

        // Nguồn
        createCell(row, 1, "Nguồn", style);

        // Số lượng Đại lý phát sinh giao dịch (merge C-F)
        createCell(row, 2, "Số lượng Đại lý phát sinh giao dịch", style);

        // Số Lượng Thuê Bao Đặt Hàng (merge G-J)
        createCell(row, 6, "Số lượng Thuê Bao Đặt Hàng", style);

        // Số lượng Thuê Bao Mua Gói (merge K-N)
        createCell(row, 10, "Số lượng Thuê Bao Mua Gói", style);
    }

    private static void createSummaryAllSubHeaders(Row row, CellStyle style, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // STT - merged with main header, so skip
        // Nguồn - merged with main header, so skip

        // Số lượng Đại lý phát sinh giao dịch
        createCell(row, 2, "Tổng đại lý", style);
        createCell(row, 3, formatDayRange(start, end), style);
        createCell(row, 4, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 5, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);

        // Số Lượng Thuê Bao Đặt Hàng
        createCell(row, 6, formatDayRange(start, end), style);
        createCell(row, 7, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 8, COMPARISON_TEXT, style);
        createCell(row, 9, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);

        // Số lượng Thuê Bao Mua Gói
        createCell(row, 10, formatDayRange(start, end), style);
        createCell(row, 11, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 12, COMPARISON_TEXT, style);
        createCell(row, 13, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);
    }

    private static void createSummaryByOrgMainHeaders(Row row, CellStyle style) {
        // STT
        createCell(row, 0, "STT", style);

        // Công ty khu vực
        createCell(row, 1, "Công ty khu vực", style);

        // Tên Đại lý
        createCell(row, 2, "Tên Đại lý", style);

        // Số lượng thuê bao đặt hàng (merge D-G)
        createCell(row, 3, "Số lượng thuê bao đặt hàng", style);

        // Số lượng Thuê Bao kích hoạt 900 (merge H-K)
        createCell(row, 7, "Số lượng Thuê Bao kích hoạt 900", style);

        // Doanh thu gói cước (merge L-O)
        createCell(row, 11, "Doanh thu gói cước", style);
    }

    private static void createSummaryByOrgSubHeaders(Row row, CellStyle style, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // STT - merged with main header, so skip
        // Công ty khu vực - merged with main header, so skip
        // Tên Đại lý - merged with main header, so skip

        // Số lượng thuê bao đặt hàng
        createCell(row, 3, formatDayRange(start, end), style);
        createCell(row, 4, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 5, COMPARISON_TEXT, style);
        createCell(row, 6, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);

        // Số lượng Thuê Bao kích hoạt 900
        createCell(row, 7, formatDayRange(start, end), style);
        createCell(row, 8, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 9, COMPARISON_TEXT, style);
        createCell(row, 10, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);

        // Doanh thu gói cước
        createCell(row, 11, formatDayRange(start, end), style);
        createCell(row, 12, MONTH_PREFIX + end.format(MONTH_FORMATTER), style);
        createCell(row, 13, COMPARISON_TEXT, style);
        createCell(row, 14, YEAR_PREFIX + end.format(YEAR_FORMATTER), style);
    }

    private static String formatDayRange(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return DAY_PREFIX;
        }
        if (start == null) {
            return DAY_PREFIX + end.format(DATE_FORMATTER);
        }
        if (end == null) {
            return DAY_PREFIX + start.format(DATE_FORMATTER);
        }
        if (start.isEqual(end)) {
            return DAY_PREFIX + start.format(DATE_FORMATTER);
        }
        return DAY_PREFIX + start.format(DATE_FORMATTER) + " - " + end.format(DATE_FORMATTER);
    }

    private static void createSummaryAllDataRow(Row row, SummaryAllReportDTO data, CellStyle dataStyle, CellStyle numberStyle) {
        if (data == null) {
            data = SummaryAllReportDTO.builder()
                    .source("Reseller")
                    .totalAgents(0L)
                    .totalAgentsToday(0L)
                    .totalAgentsMonth(0L)
                    .totalAgentsYear(0L)
                    .totalEsimConnectedToday(0L)
                    .totalEsimConnectedMonth(0L)
                    .totalEsimConnectedMonthGrowth(0.0)
                    .totalEsimConnectedYear(0L)
                    .totalEsimPurchasedToday(0L)
                    .totalEsimPurchasedMonth(0L)
                    .totalEsimPurchasedMonthGrowth(0.0)
                    .totalEsimPurchasedYear(0L)
                    .build();
        }

        // STT (sheet tổng hợp chỉ có 1 dòng dữ liệu -> để "1")
        createCell(row, 0, "1", dataStyle);

        // Nguồn
        createCell(row, 1, data.getSource() != null ? data.getSource() : "Reseller", dataStyle);

        // Số lượng Đại lý phát sinh giao dịch
        createCell(row, 2, data.getTotalAgents() != null ? data.getTotalAgents().toString() : "0", numberStyle);
        createCell(row, 3, data.getTotalAgentsToday() != null ? data.getTotalAgentsToday().toString() : "0", numberStyle);
        createCell(row, 4, data.getTotalAgentsMonth() != null ? data.getTotalAgentsMonth().toString() : "0", numberStyle);
        createCell(row, 5, data.getTotalAgentsYear() != null ? data.getTotalAgentsYear().toString() : "0", numberStyle);

        // Số Lượng Thuê Bao Đặt Hàng
        createCell(row, 6, data.getTotalEsimConnectedToday() != null ? data.getTotalEsimConnectedToday().toString() : "0", numberStyle);
        createCell(row, 7, data.getTotalEsimConnectedMonth() != null ? data.getTotalEsimConnectedMonth().toString() : "0", numberStyle);
        createCell(row, 8, data.getTotalEsimConnectedMonthGrowth() != null ? String.format("%.2f", data.getTotalEsimConnectedMonthGrowth()) : "", numberStyle);
        createCell(row, 9, data.getTotalEsimConnectedYear() != null ? data.getTotalEsimConnectedYear().toString() : "0", numberStyle);

        // Số lượng Thuê Bao Mua Gói
        createCell(row, 10, data.getTotalEsimPurchasedToday() != null ? data.getTotalEsimPurchasedToday().toString() : "0", numberStyle);
        createCell(row, 11, data.getTotalEsimPurchasedMonth() != null ? data.getTotalEsimPurchasedMonth().toString() : "0", numberStyle);
        createCell(row, 12, data.getTotalEsimPurchasedMonthGrowth() != null ? String.format("%.2f", data.getTotalEsimPurchasedMonthGrowth()) : "", numberStyle);
        createCell(row, 13, data.getTotalEsimPurchasedYear() != null ? data.getTotalEsimPurchasedYear().toString() : "0", numberStyle);
    }

    private static void createSummaryByOrgDataRow(Row row, SummaryByOrgReportDTO data, int stt, CellStyle dataStyle, CellStyle numberStyle, CellStyle currencyStyle) {
        boolean isTotalRow = stt == -1;
        CellStyle[] styles = resolveRowStyles(row, dataStyle, numberStyle, currencyStyle, isTotalRow);

        writeTextCell(row, 0, isTotalRow ? "" : String.valueOf(stt), styles[0]);
        writeTextCell(row, 1, nonNull(data.getRootOrgName()), styles[0]);
        writeTextCell(row, 2, nonNull(data.getOrgName()), styles[0]);

        writeNumberCell(row, 3, data.getTotalEsimOrderedToday(), styles[1]);
        writeNumberCell(row, 4, data.getTotalEsimOrderedMonth(), styles[1]);
        writeDecimalCell(row, 5, data.getTotalEsimOrderedMonthGrowth(), styles[1]);
        writeNumberCell(row, 6, data.getTotalEsimOrderedYear(), styles[1]);

        writeNumberCell(row, 7, data.getTotalEsimActivated900Today(), styles[1]);
        writeNumberCell(row, 8, data.getTotalEsimActivated900Month(), styles[1]);
        writeDecimalCell(row, 9, data.getTotalEsimActivated900MonthGrowth(), styles[1]);
        writeNumberCell(row, 10, data.getTotalEsimActivated900Year(), styles[1]);

        writeCurrencyCell(row, 11, data.getTotalRevenueToday(), styles[2]);
        writeCurrencyCell(row, 12, data.getTotalRevenueMonth(), styles[2]);
        writeDecimalCell(row, 13, data.getTotalRevenueMonthGrowth(), styles[1]);
        writeCurrencyCell(row, 14, data.getTotalRevenueYear(), styles[2]);
    }

    private static String nonNull(String value) {
        return value != null ? value : "";
    }

    private static CellStyle[] resolveRowStyles(Row row, CellStyle dataStyle, CellStyle numberStyle, CellStyle currencyStyle, boolean isTotalRow) {
        if (!isTotalRow) {
            return new CellStyle[]{dataStyle, numberStyle, currencyStyle};
        }
        Workbook wb = row.getSheet().getWorkbook();
        Font bold = wb.createFont();
        bold.setBold(true);

        CellStyle totalData = wb.createCellStyle();
        totalData.cloneStyleFrom(dataStyle);
        totalData.setFont(bold);
        addBorders(totalData);

        CellStyle totalNumber = wb.createCellStyle();
        totalNumber.cloneStyleFrom(numberStyle);
        totalNumber.setFont(bold);
        addBorders(totalNumber);

        CellStyle totalCurrency = wb.createCellStyle();
        totalCurrency.cloneStyleFrom(currencyStyle);
        totalCurrency.setFont(bold);
        addBorders(totalCurrency);

        return new CellStyle[]{totalData, totalNumber, totalCurrency};
    }

    private static void writeTextCell(Row row, int col, String value, CellStyle style) {
        createCell(row, col, value, style);
    }

    private static void writeNumberCell(Row row, int col, Long value, CellStyle style) {
        createCell(row, col, value != null ? value.toString() : "0", style);
    }

    private static void writeDecimalCell(Row row, int col, Double value, CellStyle style) {
        createCell(row, col, value != null ? String.format("%.2f", value) : "", style);
    }

    private static void writeCurrencyCell(Row row, int col, BigDecimal value, CellStyle style) {
        createCell(row, col, value != null ? formatCurrency(value) : "0 ₫", style);
    }

    private static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);

        // Apply style with borders
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            // Tạo default style với borders nếu style null
            CellStyle defaultStyle = row.getSheet().getWorkbook().createCellStyle();
            addBorders(defaultStyle);
            cell.setCellStyle(defaultStyle);
        }
    }

    /**
     * Tạo borders cho toàn bộ sheet bằng cách tạo một table style
     */
    private static void createTableBorders(SXSSFSheet sheet, int maxRow, int maxColumn) {
        // Apply borders to all cells in the range without overriding existing styles
        for (int rowIndex = 0; rowIndex <= maxRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            for (int colIndex = 0; colIndex <= maxColumn; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    cell = row.createCell(colIndex);
                }

                // Get existing style or create new one
                CellStyle existingStyle = cell.getCellStyle();
                CellStyle newStyle;

                if (existingStyle != null) {
                    // Clone existing style to preserve formatting
                    newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                } else {
                    // Create new style if none exists
                    newStyle = sheet.getWorkbook().createCellStyle();
                }

                // Add borders to the style
                addBorders(newStyle);
                cell.setCellStyle(newStyle);
            }
        }
    }

    /**
     * Tính toán dòng tổng cộng từ danh sách dữ liệu
     */
    private static SummaryByOrgReportDTO calculateTotalRow(List<SummaryByOrgReportDTO> data) {
        // Lọc bỏ dòng tổng cộng nếu đã có
        List<SummaryByOrgReportDTO> filteredData = data.stream()
                .filter(item -> !TOTAL_ROW_TEXT.equals(item.getOrgName()))
                .toList();

        long totalEsimOrderedToday = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimOrderedToday);
        long totalEsimOrderedMonth = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimOrderedMonth);
        long totalEsimOrderedYear = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimOrderedYear);

        long totalEsimActivated900Today = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimActivated900Today);
        long totalEsimActivated900Month = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimActivated900Month);
        long totalEsimActivated900Year = sumLong(filteredData, SummaryByOrgReportDTO::getTotalEsimActivated900Year);

        BigDecimal totalRevenueToday = sumBigDecimal(filteredData, SummaryByOrgReportDTO::getTotalRevenueToday);
        BigDecimal totalRevenueMonth = sumBigDecimal(filteredData, SummaryByOrgReportDTO::getTotalRevenueMonth);
        BigDecimal totalRevenueYear = sumBigDecimal(filteredData, SummaryByOrgReportDTO::getTotalRevenueYear);

        return SummaryByOrgReportDTO.builder()
                .rootOrgCode("")
                .rootOrgName("")
                .orgCode("")
                .orgName(TOTAL_ROW_TEXT)
                .totalEsimOrdered(0L)
                .totalEsimOrderedToday(totalEsimOrderedToday)
                .totalEsimOrderedMonth(totalEsimOrderedMonth)
                .totalEsimOrderedMonthGrowth(null) // Giữ null cho cột so sánh
                .totalEsimOrderedYear(totalEsimOrderedYear)
                .totalEsimActivated900(0L)
                .totalEsimActivated900Today(totalEsimActivated900Today)
                .totalEsimActivated900Month(totalEsimActivated900Month)
                .totalEsimActivated900MonthGrowth(null) // Giữ null cho cột so sánh
                .totalEsimActivated900Year(totalEsimActivated900Year)
                .totalRevenue(BigDecimal.ZERO)
                .totalRevenueToday(totalRevenueToday)
                .totalRevenueMonth(totalRevenueMonth)
                .totalRevenueMonthGrowth(null) // Giữ null cho cột so sánh
                .totalRevenueYear(totalRevenueYear)
                .build();
    }

    private static long sumLong(List<SummaryByOrgReportDTO> list, Function<SummaryByOrgReportDTO, Long> getter) {
        return list.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    private static BigDecimal sumBigDecimal(List<SummaryByOrgReportDTO> list, Function<SummaryByOrgReportDTO, BigDecimal> getter) {
        return list.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,.0f ₫", amount.doubleValue());
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private static CellStyle createSubHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        // Định dạng số nguyên không có phần thập phân
        DataFormat df = workbook.createDataFormat();
        style.setDataFormat(df.getFormat("#,##0"));
        return style;
    }

    private static CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        // Định dạng tiền tệ an toàn với Excel (tránh lỗi styles.xml)
        DataFormat df = workbook.createDataFormat();
        style.setDataFormat(df.getFormat("#,##0 \"₫\""));
        return style;
    }

    private static void addBorders(CellStyle style) {
        // Set all borders to thin style for all cells
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Set border colors to black for better visibility
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        // Không can thiệp fill pattern để tránh Excel sửa styles.xml
    }

    private static byte[] writeToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

}
