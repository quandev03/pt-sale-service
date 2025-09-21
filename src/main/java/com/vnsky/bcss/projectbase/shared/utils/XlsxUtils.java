package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.domain.dto.CommonDTO;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.constant.ConverterKeyConstant;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.excel.dto.ExcelData;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@UtilityClass
@Slf4j
public class XlsxUtils {
    public static <T> ExcelData<T> readExcel(InputStream is, Class<T> clazz) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming first sheet

            List<T> dataLines = new ArrayList<>();

            // Read header row (assumed to be the first row)
            Map<Integer, String> headerLineNames = getHeaderLineNames(sheet);


            Map<Integer, Field> hmField = ClassUtils.getMapFieldByKey(field -> {
                XlsxColumn column = field.getAnnotation(XlsxColumn.class);
                return column == null ? null : column.index();
            }, clazz);
            // Read data rows (starting from the second row)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (!isRowEmpty(dataRow)) {
                    T rowData = clazz.getConstructor().newInstance();
                    for (Cell cell : dataRow) {
                        int index = cell.getColumnIndex();
                        if (hmField.containsKey(index)) {
                            Field field = hmField.get(index);
                            Method method = getSetter(field, clazz);
                            assert method != null;
                            method.invoke(rowData, cellToString(cell));
                        }
                    }
                    dataLines.add(rowData);
                }
            }
            return new ExcelData<>(headerLineNames, dataLines);
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    public static boolean isRowEmpty(Row row) {
        if(row == null){
            return true;
        }

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if(cell.getCellType() == CellType.STRING){
                if(StringUtils.hasText(cell.getStringCellValue())){
                    return false;
                }
            }
            else if(cell.getCellType() != CellType.BLANK){
                return false;
            }
        }
        return true;
    }

    private Map<Integer, String> getHeaderLineNames(Sheet sheet) {
        Map<Integer, String> headerLineNames = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                int index = cell.getColumnIndex();
                headerLineNames.put(index, cell.getStringCellValue());
            }
        }
        return headerLineNames;
    }

    public static <T> TemporaryFileResource writeExcel(ExcelData<T> excelData, Class<T> clazz, boolean ignore) {
        return writeExcel(excelData, clazz,null, null, ignore);
    }

    public static <T> TemporaryFileResource writeExcelHaveImage(ExcelData<T> excelData, Class<T> clazz, Function<String, byte[]> imageConverter, boolean ignore) {
        return writeExcel(excelData, clazz, imageConverter, null, ignore);
    }

    @SuppressWarnings("java:S3776")
    public static <T> TemporaryFileResource writeExcel(ExcelData<T> excelData, Class<T> clazz, Function<String, byte[]> imageConverter, ConditionShowValue conditionShowValue, boolean ignore) {
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            //Create cell style for header and data
            CellStyle headerStyle;
            CellStyle dataStyle;
            headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            addBorders(headerStyle);
            dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.RIGHT);
            addBorders(dataStyle);
            // Get Field of data Object
            Set<Integer> fieldImages = new HashSet<>();
            Map<Integer, Field> hmField = getHmField(ignore, clazz, fieldImages);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            Map<Integer, Integer> columnSizeMap = new HashMap<>();
            List<Integer> columnsInx = new ArrayList<>(hmField.keySet());
            if (excelData.isIncludeRowNum()) {
                Cell cell = headerRow.createCell(0, CellType.STRING);
                cell.setCellValue("STT");
                cell.setCellStyle(headerStyle);
            }
            columnsInx.forEach(k -> {
                int cellIndex = k + (excelData.isIncludeRowNum() ? 1 : 0);
                Cell cell = headerRow.createCell(cellIndex, CellType.STRING);
                String headerValue = excelData.getHeaderLineNamesOf(k, getHeader(hmField.get(k), false));
                cell.setCellValue(headerValue);
                cell.setCellStyle(headerStyle);
                columnSizeMap.put(cellIndex, headerValue.length());
            });

            // Create Data Rows
            int rowIdx = 0;
            for (T dataObj : excelData.getDataLines()) {
                Row dataRow = sheet.createRow(++rowIdx);
                //INSERT STT COLUMN
                if (excelData.isIncludeRowNum()) {
                    Cell cell = dataRow.createCell(0, CellType.STRING);
                    cell.setCellValue(rowIdx);
                    cell.setCellStyle(headerStyle);
                }
                hmField.forEach((idx, field) -> {
                    int cellIndex = idx + (excelData.isIncludeRowNum() ? 1 : 0);
                    Cell cell = dataRow.createCell(cellIndex);
                    String value = getFieldValue(field, conditionShowValue, clazz, dataObj);
                    cell.setCellValue(value);
                    cell.setCellStyle(dataStyle);

                    boolean isImage = fieldImages.contains(cellIndex);

                    if (!CollectionUtils.isEmpty(fieldImages) && isImage) {
                        dataRow.setHeight((short) 1350);
                    }

                    Integer columnSize = columnSizeMap.get(cellIndex);
                    if(value.length() > columnSize && !isImage) {
                        columnSizeMap.put(cellIndex, value.length());
                    }

                    setValueToCell(cell, value, dataStyle, isImage, imageConverter, sheet, workbook);
                });

            }

            setSizeColumn(hmField, columnSizeMap, sheet, fieldImages);

            // Write to a ByteArrayOutputStream
            TemporaryFileResource temporaryFileResource = new TemporaryFileResource();
            try (OutputStream os = temporaryFileResource.getOutputStream()) {
                workbook.write(os);
            }
            return temporaryFileResource;
        } catch (IOException e) {
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    private static void setValueToCell(Cell cell, String value, CellStyle dataStyle, boolean isImage, Function<String, byte[]> imageConverter, Sheet sheet, Workbook workbook) {
        cell.setCellStyle(dataStyle);
        if (!isImage) {
            cell.setCellValue(value);
        } else {
            try {
                byte[] imageBytes = imageConverter.apply(value);
                int inputImage = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                SXSSFDrawing drawing = (SXSSFDrawing) sheet.createDrawingPatriarch();
                XSSFClientAnchor clientAnchor = getXssfClientAnchor(cell);
                drawing.createPicture(clientAnchor, inputImage);
            } catch (Exception e) {
                cell.setCellValue("");
            }
        }
    }

    private static @NotNull XSSFClientAnchor getXssfClientAnchor(Cell cell) {
        XSSFClientAnchor clientAnchor = new XSSFClientAnchor();
        clientAnchor.setCol1(cell.getColumnIndex());
        clientAnchor.setCol2(cell.getColumnIndex() + 1);
        clientAnchor.setRow1(cell.getRowIndex());
        clientAnchor.setRow2(cell.getRowIndex() + 1);
        clientAnchor.setDx1(9525);
        clientAnchor.setDy1(9525);
        clientAnchor.setDx2(4000);
        clientAnchor.setDx2(2*9525);
        return clientAnchor;
    }

    private static void setSizeColumn(Map<Integer, Field> hmField, Map<Integer, Integer> columnSizeMap, Sheet sheet, Set<Integer> fieldImages) {
        hmField.keySet().forEach(index -> {
            if (fieldImages.contains(index)) {
                sheet.setColumnWidth(index, 4500);
            } else {
                Integer columnSize = columnSizeMap.get(index);
                columnSize = columnSize == null ? 0 : columnSize;
                int width = Math.min(((int) (columnSize * 1.14388)) * 256, 255 * 256);
                sheet.setColumnWidth(index , width);
            }
        });
    }

    public static <T> byte[] writeCsv(ExcelData<T> excelData, Class<T> clazz, boolean ignore) {
        return writeCsv(excelData, clazz, null, ignore);
    }

    public static <T> byte[] writeCsv(ExcelData<T> excelData, Class<T> clazz, ConditionShowValue conditionShowValue, boolean ignore) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {

            Map<Integer, Field> hmField = getHmField(ignore, clazz, new HashSet<>());

            List<Integer> columnsInx = new ArrayList<>(hmField.keySet().stream().sorted(Integer::compareTo).toList());
            List<String> header = new ArrayList<>();
            if (excelData.isIncludeRowNum()) {
                header.add("STT");
            }
            for (Integer inx : columnsInx) {
                header.add(excelData.getHeaderLineNamesOf(inx, getHeader(hmField.get(inx), true)));
            }
            writer.write(String.join(",", header));
            writer.write(System.lineSeparator());

            // Create Data Rows
            int rowIdx = 0;
            for (T dataObj : excelData.getDataLines()) {
                List<String> builderData = new ArrayList<>();
                //INSERT STT COLUMN
                if (excelData.isIncludeRowNum()) {
                    builderData.add(String.valueOf(++rowIdx));
                }

                for (Integer inx : columnsInx) {
                    Field field = hmField.get(inx);
                    Object value = getFieldValue(field, conditionShowValue, clazz, dataObj);
                    builderData.add(String.format("'%s'", value != null ? value.toString() : ""));
                }
                writer.write(String.join(",", builderData));
                writer.write(System.lineSeparator());

            }

            return bos.toByteArray();
        } catch (IOException e) {
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    private <T> String getFieldValue(Field field, ConditionShowValue conditionShowValue, Class<T> clazz, T dataObj) {
        ReflectionUtils.makeAccessible(field);
        boolean shouldShowValue = true;
        if (conditionShowValue != null) {
            shouldShowValue = conditionShowValue.willShowValue(clazz, dataObj);
        }
        Object value = "";
        if (shouldShowValue) {
            try {
                value = field.get(dataObj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
            XlsxColumn xlsxColumn = field.getAnnotation(XlsxColumn.class);
            value = convertValue(value, xlsxColumn);
        }
        return value.toString();
    }

    private static Object convertValue(Object value, XlsxColumn xlsxColumn) {
        if (StringUtils.hasText(xlsxColumn.converter())) {
            try {
                value = ConverterKeyConstant.convert(xlsxColumn.converter(), value);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return value == null ? "" : value;
    }

    private <T> Method getSetter(Field field, Class<T> clazz) {
        try {
            String capitalizeFieldName = StringUtils.capitalize(field.getName());
            Method getter = clazz.getDeclaredMethod("get" + capitalizeFieldName);
            return clazz.getDeclaredMethod("set" + capitalizeFieldName, getter.getReturnType());
        } catch (Exception e) {
            if (clazz.getSuperclass() != null) {
                return getSetter(field, clazz.getSuperclass());
            }
            log.info(e.getMessage(), e);
        }
        return null;
    }

    private static void addBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // Helper method to convert cell value to string
    private static String cellToString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue().isBlank() ? null : cell.getStringCellValue().trim();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    String data = String.format(Constant.UploadFile.FORMAT_NUMBER, cell.getNumericCellValue());
                    return data.isBlank() ? null : data.trim();
                }
            }
            case BOOLEAN -> {
                return Boolean.toString(cell.getBooleanCellValue());
            }
            case FORMULA -> {
                return cell.getCellFormula();
            }
            default -> {
                return null;
            }
        }
    }

    private String getHeader(Field field, boolean csv) {
        XlsxColumn column = field.getAnnotation(XlsxColumn.class);
        if (column == null) {
            return "";
        }
        return csv ? column.headerCsv() : column.header();
    }

    private static Map<Integer, Field> getHmField(boolean ignore, Class<?> clazz, Set<Integer> fieldImages) {
        ConditionGetKeyOfField<Integer> conditionGetKeyOfField = field -> {
            XlsxColumn column = field.getAnnotation(XlsxColumn.class);
            if (column == null) {
                return null;
            } else {
                if(column.isImageColumn()) {
                    fieldImages.add(column.index());
                }
                if (ignore) {
                    return column.ignore() ? null : column.ignoreIndex();
                } else {
                    return column.index();
                }
            }
        };
        Map<Integer, Field> hmField = ClassUtils.getMapFieldByKey(conditionGetKeyOfField, clazz);
        // if Class extends CommonDTO class add commonDTO field to list field
        if (CommonDTO.class.isAssignableFrom(clazz)) {
            Integer max = hmField.keySet().stream().max(Comparator.comparing(Integer::intValue)).orElse(0);
            Map<Integer, Field> hmFieldCommon = ClassUtils.getMapFieldByKey(conditionGetKeyOfField, CommonDTO.class);
            hmFieldCommon.forEach((k, v) -> hmField.put(k + max, v));
        }
        return hmField;
    }
}
