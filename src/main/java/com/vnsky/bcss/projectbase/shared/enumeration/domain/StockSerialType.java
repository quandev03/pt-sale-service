package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum StockSerialType {
    HEADQUARTER("00", "Trụ sở chính"),
    DEPARTMENT("01", "Phòng ban"),
    CENTER("02", "Trung tâm"),
    BRANCH("03", "Chi nhánh"),
    STORE("04", "Cửa hàng"),
    UNKNOWN(null, "Không xác định")
    ;

    private final String value;
    private final String description;

    public static final Map<String, StockSerialType> VALUE_MAP;

    static {
        Map<String, StockSerialType> tmpMap = new HashMap<>();
        for (StockSerialType value : values()) {
            tmpMap.put(value.getValue(), value);
        }
        VALUE_MAP = Collections.unmodifiableMap(tmpMap);
    }

    public static StockSerialType fromValue(String value) {
        return VALUE_MAP.get(value) == null ?  UNKNOWN : VALUE_MAP.get(value);
    }

}
