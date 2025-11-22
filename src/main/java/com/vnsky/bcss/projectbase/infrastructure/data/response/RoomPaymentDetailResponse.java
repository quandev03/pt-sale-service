package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@Schema(description = "Response chi tiết thanh toán dịch vụ")
public class RoomPaymentDetailResponse {
    @Schema(description = "ID chi tiết")
    String id;

    @Schema(description = "Loại dịch vụ")
    RoomServiceType serviceType;

    @Schema(description = "Tên dịch vụ")
    String serviceName;

    @Schema(description = "Số lượng")
    BigDecimal quantity;

    @Schema(description = "Đơn giá")
    BigDecimal unitPrice;

    @Schema(description = "Thành tiền")
    BigDecimal amount;
}

