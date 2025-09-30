package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubscriberActiveStatus {
    ACTIVE(1, "Không bị chặn"),
    BLOCK_1_WAY_BY_REQUEST(10, "Chặn một chiều do yêu cầu"),
    BLOCK_2_WAYS_BY_REQUEST(20, "Chặn hai chiều do yêu cầu"),
    BLOCK_1_WAY_BY_MBF(11, "Chặn một chiều do nhà mạng"),
    BLOCK_2_WAYS_BY_MBF(21, "Chặn hai chiều do nhà mạng"),
    UNKNOWN(99, "Mạng không xác định trạng thái");
    ;

    private final Integer value;

    private final String description;

    public static SubscriberActiveStatus of(Integer value) {
        for (SubscriberActiveStatus status : values()) {
            if(status.getValue().equals(value)) return status;
        }
        return UNKNOWN;
    }
}
