package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    ACTIVE(1, "Hoạt động"),
    INACTIVE(0, "Không hoạt động");

    private final Integer value;
    private final String description;
}
