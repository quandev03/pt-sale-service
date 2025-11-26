package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AdvertisementStatus {
    ACTIVE("ACTIVE", "Đang hoạt động"),
    INACTIVE("INACTIVE", "Không hoạt động"),
    PUBLISHED("PUBLISHED", "Đã xuất bản"),
    DRAFT("DRAFT", "Bản nháp");

    private final String code;
    private final String description;

    public static AdvertisementStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown advertisement status: " + value));
    }
}


