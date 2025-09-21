package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EsimRegistrationStatus {
    UN_USED(0, "Chưa sử dụng"),
    ACTIVED(1, "Đang hoạt động"),
    WAITING_SUBMIT_INFO(2, "Chờ đăng ký thông tin"),
    WAITING_SYNC_DATA_MBF(3, "Chờ đồng bộ thông tin phía MBF"),
    SYNCED_DATA_FAILED_MBF(4, "Đồng bộ thông tin thất bại phía MBF"),
    ;

    private final Integer value;
    private final String description;
}
