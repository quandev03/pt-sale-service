package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum NumberTransactionType {

    UPLOAD(1, "Upload", "Danh_sach_upload_so"),
    TRANSFER(2, "Điều chuyển số", "Danh_sach_dieu_chuyen_so"),
    REVOKE(3, "Thu hồi", "Danh_sach_thu_hoi_so"),
    COMBINING_KIT(4, "Ghép KIT", "Dau_KIT"),
    CLASSIFY_SPECIAL(5, "Gán số đặc biệt", "Danh_sach_gan_so_dac_biet"),
    DISTRIBUTE(6, "Phân phối số", "Danh_sach_phan_phoi_so"),
    KEEP(7, "Giữ số", "Giu_so"),
    UNKEEP(8, "Bỏ giữ số", "Bo_giu_so"),
    EXPORT_FOR_PARTNER(9, "Xuất số cho đối tác", "Danh_sach_xuat_so_cho_doi_tac"),
    EXPORT_KIT_FOR_PARTNER(10,"Xuất KIT cho đối tác", "Xuat_kit_cho_doi_tac"),
    UNKNOWN(null, "Không xác định", "Khong_xac_dinh"),
    CANCEL_COMBINIG_KIT(11, "Hủy ghép Kit", "Huy_ghep_Kit"),
    ;

    private final Integer value;

    private final String description;

    private final String unaccentDescription;

    public static final Map<Integer, NumberTransactionType> VALUE_MAP;

    static {
        Map<Integer, NumberTransactionType> tmpMap = new HashMap<>();
        for (NumberTransactionType value : values()) {
            tmpMap.put(value.getValue(), value);
        }
        VALUE_MAP = Collections.unmodifiableMap(tmpMap);
    }

    public static NumberTransactionType fromValue(Integer value) {
        if (!VALUE_MAP.containsKey(value)) {
            return UNKNOWN;
        }
        return VALUE_MAP.get(value);
    }

}
