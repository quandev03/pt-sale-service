package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumberUploadStatus {

    PROCESSING(1, "Đang kiểm tra"),
    FAILURE(2, Constant.MESSAGE_FAILURE),
    SUCCESS(3, Constant.MESSAGE_SUCCESS)
    ;

    private final int value;

    private final String description;

}
