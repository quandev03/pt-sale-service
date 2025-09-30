package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumberTransactionStatus {
    PRE_START(0, "Chờ thực hiện"),
    PROCESSING(1, "Đang thực hiện"),
    COMPLETE(2, "Hoàn thành")
    ;

    private final int value;

    private final String description;

}
