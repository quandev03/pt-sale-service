package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
@Slf4j
public class DateUtils {

    public String convertDateToString(Date date, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } catch (Exception ex) {
            log.error("convertDateToString ex" + ex.getMessage(), ex);
            return null;
        }
    }

    public String convertToString(String date, String patternFrom, String patternTo) {
        try {
            SimpleDateFormat formatFrom = new SimpleDateFormat(patternFrom);
            SimpleDateFormat formatTo = new SimpleDateFormat(patternTo);
            return formatTo.format(formatFrom.parse(date));
        } catch (Exception ex) {
            String errorMsg = String.format("convertToString error: %s, date: %s, patternFrom: %s, patternTo: %s", ex.getMessage(), date, patternFrom, patternTo);
            log.error(errorMsg, ex);
            return null;
        }
    }

    public Date convertToDate(String date, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(date);
        } catch (Exception ex) {
            String errorMsg = String.format("convertToDate error: %s, date: %s, pattern: %s", ex.getMessage(), date, pattern);
            log.error(errorMsg, ex);
            return null;
        }
    }

    public Date convertToDateWithPatternTimeWithUTC(String date) {
        return convertToDate(date, Constant.TIMESTAMP_DATE_PATTERN_UTC);
    }

    public LocalDateTime convertToLocalDateTime(String date, String pattern) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(date, format);
        } catch (Exception ex) {
            String errorMsg = String.format("convertToLocalDateTime error: %s, date: %s, pattern: %s", ex.getMessage(), date, pattern);
            log.error(errorMsg, ex);
            return null;
        }
    }

    public LocalDateTime convertToLocalDateTimeWithPatternTimeWithUTC(String date) {
        return convertToLocalDateTime(date, Constant.TIMESTAMP_DATE_PATTERN_UTC);
    }

    public LocalDate convertToLocalDateWithPattern(String date, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            log.error("convertToLocalDateWithPattern error: " + e.getMessage(), e);
            return null;
        }
    }

    public LocalDate toDate(String date, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            log.error("convertToLocalDateWithPattern error: " + e.getMessage(), e);
            return null;
        }
    }

    public LocalDate toDate(String date) {
        return toDate(date, "DD/MM/YYYY");
    }

    public long convertDateStringToMilisecondWithPatternUTC(String date) {
        try {
            LocalDateTime localDateTime = convertToLocalDateTimeWithPatternTimeWithUTC(date);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ex) {
            String errorMsg = String.format("convertDateStringToMilisecond error: %s, date: %s", ex.getMessage(), date);
            log.error(errorMsg, ex);
            return 0;
        }
    }

    public LocalDateTime currentDateLocalDateTime() {
        return LocalDateTime.now();
    }

    public LocalDate currentLocalDate() {
        return LocalDate.now();
    }

    public static String getDateDayAndMonthAndYear(LocalDateTime localDateTime) {
        return localDateTime.getDayOfMonth() + "-" + localDateTime.getMonthValue() + "-" + localDateTime.getYear();
    }

    public static LocalDateTime startDayWithHour(int hour) {
        return currentDateLocalDateTime().withHour(hour).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime startDay(LocalDate localDate) {
        return localDate.atTime(LocalTime.MIN);
    }

    public static LocalDateTime endDay(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX);
    }

    public static boolean validFormatStringWithPattern(String date, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.parse(date);
            return true;
        } catch (Exception ex) {
            log.error("validFormatStringWithPattern ex" + ex.getMessage(), ex);
            return false;
        }
    }

    public static String localDateToString(LocalDate date, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return formatter.format(date);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String localDateTimeToString(LocalDateTime date, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return formatter.format(date);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString, boolean isFrom) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN);
            LocalDate date = LocalDate.parse(dateString, formatter);
            if (isFrom) return date.atStartOfDay();
            else return date.atTime(23,59,59);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN);
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate.atStartOfDay();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN);
        return localDateTime.format(formatter);
    }

    public static String convertToFileNameExportExcel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.TIME_STAMP_EXPORT_DATE);
        return "-" + LocalDateTime.now().format(formatter);
    }

    public static String setEmptyIfError(String date, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dateFormat.parse(date);
            return date;
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return Strings.EMPTY;
    }

    public static Instant localDateTimeToInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(Constant.FE_DATE_PATTERN);
        SimpleDateFormat outputFormat = new SimpleDateFormat(Constant.TIMESTAMP_DATE_PATTERN_DEFAULT);
        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return Strings.EMPTY;
    }
}
