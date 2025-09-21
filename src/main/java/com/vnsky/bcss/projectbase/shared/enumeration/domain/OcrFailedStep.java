package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OcrFailedStep {
    OCR(1, "OCR"),
    FACE_CHECK(2, "FACE CHECK");

    private final int step;
    private final String description;
}
