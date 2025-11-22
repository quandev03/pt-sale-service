package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
@Schema(description = "Response dịch vụ phòng")
public class RoomServiceResponse {
    @Schema(description = "ID dịch vụ")
    String id;

    @Schema(description = "Mã đơn vị tổ chức (phòng)")
    String orgUnitId;

    @Schema(description = "Loại dịch vụ")
    RoomServiceType serviceType;

    @Schema(description = "Mã dịch vụ")
    String serviceCode;

    @Schema(description = "Tên dịch vụ")
    String serviceName;

    @Schema(description = "Giá dịch vụ")
    BigDecimal price;

    @Schema(description = "Client ID")
    String clientId;

    @Schema(description = "Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    Integer status;

    @Schema(description = "Người tạo")
    String createdBy;

    @Schema(description = "Ngày tạo")
    LocalDateTime createdDate;

    @Schema(description = "Người cập nhật")
    String modifiedBy;

    @Schema(description = "Ngày cập nhật")
    LocalDateTime modifiedDate;
}

