package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.Getter;

@Getter
public enum EnumSalePackage {
    PARTNER(1),
    CUSTOMER(2);

    private Integer value;

    EnumSalePackage(Integer value) {
        this.value = value;
    }

    public static EnumSalePackage getType(Integer value) {
        return switch (value) {
            case 1 -> PARTNER;
            case 2 -> CUSTOMER;
            default -> throw new IllegalArgumentException("Loại phiếu không hợp lệ: ");
        };
    }
}