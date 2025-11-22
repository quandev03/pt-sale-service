package com.vnsky.bcss.projectbase.infrastructure.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Request upload file Excel thanh toán dịch vụ phòng")
public class RoomPaymentUploadRequest {
    @NotNull(message = "File Excel không được để trống")
    @Schema(description = "File Excel chứa thông tin sử dụng dịch vụ", required = true)
    private MultipartFile file;

    @NotNull(message = "Tháng không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1 đến 12")
    @Max(value = 12, message = "Tháng phải từ 1 đến 12")
    @Schema(description = "Tháng thanh toán (1-12)", required = true)
    private Integer month;

    @NotNull(message = "Năm không được để trống")
    @Min(value = 2020, message = "Năm không hợp lệ")
    @Schema(description = "Năm thanh toán", required = true)
    private Integer year;
}

