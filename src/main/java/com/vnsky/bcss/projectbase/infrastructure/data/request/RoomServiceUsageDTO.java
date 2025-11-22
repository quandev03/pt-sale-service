package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.shared.utils.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomServiceUsageDTO {
    @XlsxColumn(index = 0)
    private String roomCode; // Mã phòng

    @XlsxColumn(index = 1)
    private BigDecimal electricityUsage; // Số điện sử dụng

    @XlsxColumn(index = 2)
    private BigDecimal waterUsage; // Số nước sử dụng

    @XlsxColumn(index = 3)
    private BigDecimal vehicleCount; // Số xe
}

