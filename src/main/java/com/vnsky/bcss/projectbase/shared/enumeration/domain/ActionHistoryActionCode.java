package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActionHistoryActionCode {
    VERIFY_INFO("VERIFY_INFO", "Kích hoạt thuê bao"),
    UPDATE_INFO("UPDATE_INFO", "Cập nhật thông tin thuê bao"),
    CALL_900("CALL_900", "Gọi 900 kích hoạt thuê bao"),
    GEN_QR_CODE("GEN_QR_CODE", "Tạo mã QR cho eSIM"),
    SEND_QR_CODE("SEND_QR_CODE", "Gửi mã QR"),
    BOOK_ESIM("BOOK_ESIM", "Đặt hàng eSIM"),
    CRAFT_KIT("CRAFT_KIT", "Đấu nối cho eSIM"),
    CREATE_SALE_ORDER("CREATE_SALE_ORDER", "Tạo đơn hàng bán"),
    REGISTER_PACKAGE("REGISTER_PACKAGE", "Đăng ký gói cước"),
    DELETE_PACKAGE("DELETE_PACKAGE", "Hủy gói cước"),
    ACTION_TYPE("VIEW", "Hiển thị màn hình")
    ;

    private final String code;

    private final String description;
}
