package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NumberProcessType {

    INDIVIDUAL(1, "Đơn lẻ"),
    BATCH(2, "Theo lô"),
    ONLINE(4, "Đơn hàng online"),
    UNKNOWN(-1, "Unknown");
    ;

    private final int value;

    private final String description;

    public static NumberProcessType fromValue(Integer value) {
        return Arrays.stream(values()).filter(e -> e.getValue() == value).findFirst().orElse(UNKNOWN);
    }
}
