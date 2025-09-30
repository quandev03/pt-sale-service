package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumberStepStatus {
    UPLOADED(1, "Đã upload"),
    CHECKED(2, "Đã kiểm tra"),
    PROCESSED(3, "Đã xử lý ra kết quả")
    ;

    private final int value;

    private final String description;

}
