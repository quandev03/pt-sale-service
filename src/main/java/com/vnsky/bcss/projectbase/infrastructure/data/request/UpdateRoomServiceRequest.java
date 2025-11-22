package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request để cập nhật dịch vụ phòng")
public class UpdateRoomServiceRequest {

    @Schema(description = "Mã đơn vị tổ chức (phòng)")
    private String orgUnitId;

    @Schema(description = "Loại dịch vụ: ELECTRICITY, WATER, INTERNET, OTHER, ROOM_RENT")
    private RoomServiceType serviceType;

    @Schema(description = "Mã dịch vụ (bắt buộc nếu serviceType = OTHER, tự động nếu là ELECTRICITY/WATER/INTERNET/ROOM_RENT)")
    private String serviceCode;

    @Schema(description = "Tên dịch vụ (bắt buộc nếu serviceType = OTHER, tự động nếu là ELECTRICITY/WATER/INTERNET/ROOM_RENT)")
    private String serviceName;

    @Positive(message = "Giá dịch vụ phải lớn hơn 0")
    @Schema(description = "Giá dịch vụ")
    private BigDecimal price;

    @Schema(description = "Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    private Integer status;
}

