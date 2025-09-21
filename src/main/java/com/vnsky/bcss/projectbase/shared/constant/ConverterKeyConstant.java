package com.vnsky.bcss.projectbase.shared.constant;

import com.vnsky.bcss.projectbase.shared.utils.XlsxDataConverter;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.vnsky.bcss.projectbase.shared.constant.Constant.COMMON_DATE_TIME_FORMAT;
import static com.vnsky.bcss.projectbase.shared.constant.Constant.COMMON_DATE_TIME_FORMAT_ZONE_FORMATER;

@UtilityClass
public class ConverterKeyConstant {
    public static final String LOCAL_DATE_TIME_TO_STRING_CONVERTER = "localDateTimeToStringConverter";
    public static final String INSTANT_TO_STRING_CONVERTER = "instantToStringConverter";
    private static final Map<String, XlsxDataConverter> converterMap = new HashMap<>();

    private static final XlsxDataConverter localDateTimeToStringConverter = new XlsxDataConverter() {
        @Override
        public <T> String convert(T date) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_DATE_TIME_FORMAT);
            return ((LocalDateTime) date).format(formatter);
        }
    };

    private static final XlsxDataConverter instantToStringConverter = new XlsxDataConverter() {
        @Override
        public <T> String convert(T time) {
            return time == null ? null : COMMON_DATE_TIME_FORMAT_ZONE_FORMATER.format((Instant) time);
        }
    };

    static {
        converterMap.put(LOCAL_DATE_TIME_TO_STRING_CONVERTER, localDateTimeToStringConverter);
        converterMap.put(INSTANT_TO_STRING_CONVERTER, instantToStringConverter);
    }

    public static Object convert(String key, Object value) {
        return converterMap.get(key).convert(value);
    }


}
