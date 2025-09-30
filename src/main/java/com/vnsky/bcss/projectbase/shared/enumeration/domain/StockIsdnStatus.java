package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberStatusResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum StockIsdnStatus {
    UNUSED(1, "Chưa sử dụng"),
    IN_STOCK(2, "Trong kho"),
    SOLD(3, "Đã bán"),
    CALLED_900(4, "Đã gọi 900"),
    UPDATED_TTTB(5, "Đã cập nhật TTTB");

    private final Integer value;
    private final String description;

    // Lấy danh sách trạng thái
    public static List<SubscriberStatusResponse> getStatusList() {
        return Arrays.stream(values())
            .map(StockIsdnStatus::toResponse)
            .toList();
    }

    public SubscriberStatusResponse toResponse() {
        return new SubscriberStatusResponse(this.description, this.value);
    }

    // Lấy enum từ value
    public static StockIsdnStatus fromValue(Integer value) {
        return Arrays.stream(values())
            .filter(s -> s.getValue().equals(value))
            .findFirst()
            .orElse(null);
    }

    public static StockIsdnStatus fromResponse(SearchSubscriberResponse sub) {
        if (sub.getVerifiedStatus() != null && sub.getVerifiedStatus() == 1) {
            return UPDATED_TTTB;
        } else if (sub.getStatusCall900() != null && sub.getStatusCall900() == 1) {
            return CALLED_900;
        } else if (sub.getBoughtStatus() != null && sub.getBoughtStatus() == 1) {
            return SOLD;
        } else if (sub.getStatus() != null && sub.getStatus() == 1) {
            return IN_STOCK;
        } else {
            return UNUSED;
        }
    }
}
