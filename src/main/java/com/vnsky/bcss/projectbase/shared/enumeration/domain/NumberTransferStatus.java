package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumberTransferStatus {
    IN_STOCK(1, "Trong kho"),
    WAITING_APPROVED(2, "Chờ phê duyệt");

    private final int value;

    private final String description;

}
