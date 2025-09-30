package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ApprovalStatus {
    WAITING_APPROVAL(1, "Chờ phê duyệt"),
    APPROVING(2, "Đang phê duyệt"),
    APPROVED(3, "Đã phê duyệt"),
    DECLINE(4, "Từ chối"),
    CANCEL(5, "Hủy"),
    UNKNOWN(null, "Không xác đinh");

    private final Integer code;

    private final String message;

    public static final Map<Integer, ApprovalStatus> VALUE_MAP;

    static {
        Map<Integer, ApprovalStatus> tmpMap = new HashMap<>();
        for (ApprovalStatus value : values()) {
            tmpMap.put(value.getCode(), value);
        }
        VALUE_MAP = Collections.unmodifiableMap(tmpMap);
    }

    public static ApprovalStatus fromValue(int value) {
        if (!VALUE_MAP.containsKey(value)) {
            return UNKNOWN;
        }
        return VALUE_MAP.get(value);
    }

}
