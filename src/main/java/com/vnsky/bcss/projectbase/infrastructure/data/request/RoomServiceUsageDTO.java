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
    @XlsxColumn(index = 0, header = "Mã phòng")
    private String roomCode; // Mã phòng

    @XlsxColumn(index = 1, header = "Số điện sử dụng")
    private BigDecimal electricityUsage; // Số điện sử dụng

    @XlsxColumn(index = 2, header = "Số nước sử dụng")
    private BigDecimal waterUsage; // Số nước sử dụng

    @XlsxColumn(index = 3, header = "Số xe")
    private BigDecimal vehicleCount; // Số xe
}

