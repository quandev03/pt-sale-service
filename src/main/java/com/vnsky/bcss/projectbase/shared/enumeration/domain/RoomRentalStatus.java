package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoomRentalStatus {
    RENTED("RENTED", "Đã thuê"),
    AVAILABLE("AVAILABLE", "Chưa thuê"),
    MAINTENANCE("MAINTENANCE", "Bảo trì");

    private final String code;
    private final String description;

    public static RoomRentalStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown room rental status: " + value));
    }
}


