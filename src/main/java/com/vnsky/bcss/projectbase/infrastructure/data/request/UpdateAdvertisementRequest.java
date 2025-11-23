package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request cập nhật quảng cáo")
public class UpdateAdvertisementRequest {

    @Schema(description = "Tên quảng cáo")
    private String title;

    @Schema(description = "Nội dung quảng cáo")
    private String content;

    @Schema(description = "URL ảnh quảng cáo (từ MinIO)")
    private String imageUrl;

    @Schema(description = "Thời gian bắt đầu")
    private LocalDateTime startDate;

    @Schema(description = "Thời gian kết thúc")
    private LocalDateTime endDate;

    @Schema(description = "Trạng thái: ACTIVE, INACTIVE, PUBLISHED, DRAFT")
    private AdvertisementStatus status;
}

