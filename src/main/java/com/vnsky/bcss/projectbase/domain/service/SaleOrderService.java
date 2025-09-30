package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.port.primary.SaleOrderServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrderRevenueReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
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
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleOrderService implements SaleOrderServicePort {
    private final SaleOrderRepoPort saleOrderRepoPort;

    private static final String FILE_EXCEL_ORDER_SHEET = "Báo cáo đơn đặt hàng eSIM";
    private static final String FILE_EXCEL_ORDER_PRIVATE_SHEET = "Báo cáo đơn hàng đối tác";
    private static final String FILE_EXCEL_ORDER_TITLE = "BÁO CÁO ĐƠN ĐẶT HÀNG eSIM";
    private static final String FILE_EXCEL_ORDER_PRIVATE_TITLE = "BÁO CÁO ĐƠN HÀNG ĐỐI TÁC";



    @Override
    public Page<OrderRevenueReportResponse> searchOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        return saleOrderRepoPort.searchOrderRevenueReport(currentOrgCode, request, pageable);
    }

    @Override
    public ByteArrayOutputStream exportOrderRevenueReport(
        String currentOrgCode, SearchRevenueReportRequest request) {

        log.info("Bắt đầu xuất Excel cho Báo cáo đơn hàng đối tác");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(currentOrgCode != null ? FILE_EXCEL_ORDER_SHEET : FILE_EXCEL_ORDER_PRIVATE_SHEET);
            List<OrderRevenueReportResponse> data =
                saleOrderRepoPort.getOrderRevenueReport(currentOrgCode, request);

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
            headerStyle.setWrapText(true);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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
            titleCell.setCellValue(currentOrgCode != null ? FILE_EXCEL_ORDER_TITLE : FILE_EXCEL_ORDER_PRIVATE_TITLE);
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
                "STT", "Mã đơn hàng", "Mã đại lý", "Tên đại lý",
                "Tổng tiền gói cước gán thành công", "Số lượng eSIM",
                "eSIM đặt thành công", "Người tạo", "Ngày đặt hàng"
            };

            if (currentOrgCode == null) headers = new String[]{
                "STT", "Mã đơn hàng", "Mã đối tác", "Tên đối tác",
                "Tổng tiền gói cước gán thành công", "Số lượng eSIM",
                "eSIM đặt thành công", "Người đặt hàng", "Ngày đặt hàng"
            };

            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ======================== Data ========================
            int stt = 1;
            for (OrderRevenueReportResponse order : data) {
                Row row = sheet.createRow(rowIdx++);
                int colIdx = 0;

                row.createCell(colIdx).setCellValue(stt++);
                row.getCell(colIdx++).setCellStyle(centerStyle);

                row.createCell(colIdx).setCellValue(defaultStr(order.getOrderNo()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(defaultStr(order.getOrgCode()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(defaultStr(order.getOrgName()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                row.createCell(colIdx).setCellValue(order.getAmountTotal() != null ? order.getAmountTotal().doubleValue() : 0);
                row.getCell(colIdx++).setCellStyle(numberStyle);

                row.createCell(colIdx).setCellValue(order.getQuantity() != null ? order.getQuantity().doubleValue() : 0);
                row.getCell(colIdx++).setCellStyle(numberStyle);

                row.createCell(colIdx).setCellValue(order.getSucceededNumber() != null ? order.getSucceededNumber().doubleValue() : 0);
                row.getCell(colIdx++).setCellStyle(numberStyle);

                row.createCell(colIdx).setCellValue(defaultStr(order.getCreatedBy()));
                row.getCell(colIdx++).setCellStyle(textStyle);

                String orderDateStr = "";
                if (order.getOrderDate() != null) {
                    orderDateStr = order.getOrderDate().format(dateFormatter);
                }
                row.createCell(colIdx).setCellValue(orderDateStr);
                row.getCell(colIdx).setCellStyle(centerStyle);
            }

            // ======================== Set Column Widths ========================
            int[] columnWidths = {8, 20, 15, 40, 15, 15, 15, 20, 15};
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, columnWidths[i] * 360);
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

    private String defaultStr(String input) {
        return input != null ? input : "";
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

    @Override
    public List<StatisticResponse> statisticEsimSold(String orgCode, String startDate, String endDate, int granularity) {
        return saleOrderRepoPort.statisticEsimSold(orgCode, startDate, endDate, granularity);
    }

    @Override
    public List<StatisticOrgResponse> statisticEsimSoldOrg(String orgCode, String startDate, String endDate) {
        return saleOrderRepoPort.statisticEsimSoldOrg(orgCode, startDate, endDate);
    }

    @Override
    public BigDecimal revenue(String orgCode) {
        return saleOrderRepoPort.revenue(orgCode);
    }
}
