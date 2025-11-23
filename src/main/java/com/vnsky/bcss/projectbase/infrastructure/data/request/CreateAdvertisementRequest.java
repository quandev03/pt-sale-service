package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request tạo quảng cáo")
public class CreateAdvertisementRequest {

    @NotBlank(message = "Tên quảng cáo không được để trống")
    @Schema(description = "Tên quảng cáo", required = true)
    private String title;

    @Schema(description = "Nội dung quảng cáo")
    private String content;

    @Schema(description = "URL ảnh quảng cáo (từ MinIO)")
    private String imageUrl;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Schema(description = "Thời gian bắt đầu", required = true)
    private LocalDateTime startDate;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Schema(description = "Thời gian kết thúc", required = true)
    private LocalDateTime endDate;

    @Schema(description = "Trạng thái: ACTIVE, INACTIVE, PUBLISHED, DRAFT")
    private AdvertisementStatus status;
}

