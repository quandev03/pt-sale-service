package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Call900Status {
    CALLED(1, "Đã gọi"),
    NOT_CALL_YET(0, "Chưa gọi");

    private final Integer value;
    private final String description;
}
