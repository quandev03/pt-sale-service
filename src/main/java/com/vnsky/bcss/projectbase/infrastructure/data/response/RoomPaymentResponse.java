package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomPaymentDetailResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@Schema(description = "Response thanh toán dịch vụ phòng")
public class RoomPaymentResponse {
    @Schema(description = "ID thanh toán")
    String id;

    @Schema(description = "Mã đơn vị tổ chức (phòng)")
    String orgUnitId;

    @Schema(description = "Tháng")
    Integer month;

    @Schema(description = "Năm")
    Integer year;

    @Schema(description = "Tổng số tiền")
    BigDecimal totalAmount;

    @Schema(description = "URL QR code")
    String qrCodeUrl;

    @Schema(description = "Trạng thái (0: Chưa thanh toán, 1: Đã thanh toán)")
    Integer status;

    @Schema(description = "Ngày thanh toán")
    LocalDateTime paymentDate;

    @Schema(description = "Chi tiết thanh toán")
    List<RoomPaymentDetailResponse> details;

    @Schema(description = "Người tạo")
    String createdBy;

    @Schema(description = "Ngày tạo")
    LocalDateTime createdDate;
}

