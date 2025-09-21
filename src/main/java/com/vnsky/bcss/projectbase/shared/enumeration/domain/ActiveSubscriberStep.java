package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActiveSubscriberStep {
    VERIFIED_OCR(0, "Đã xác thực OCR"),
    VERIFIED_FACE(1, "Đã xác thực mặt"),
    WAITING_SUBMIT(2, "Chờ xác nhận"),
    DONE(3, "Hoàn thành");

    private final Integer value;

    private final String description;
}
