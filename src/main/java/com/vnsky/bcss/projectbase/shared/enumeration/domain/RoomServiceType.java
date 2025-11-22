package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoomServiceType {
    ELECTRICITY("ELECTRICITY", "Điện"),
    WATER("WATER", "Nước"),
    INTERNET("INTERNET", "Mạng"),
    OTHER("OTHER", "Khác"),
    ROOM_RENT("ROOM_RENT", "Tiền phòng");

    private final String code;
    private final String description;

    public static RoomServiceType fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(type -> type.getCode().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown room service type: " + value));
    }

    public String getServiceName() {
        return this.description;
    }
}

