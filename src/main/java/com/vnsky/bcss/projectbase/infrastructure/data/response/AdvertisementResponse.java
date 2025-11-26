package com.vnsky.bcss.projectbase.infrastructure.data.response;

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
@Schema(description = "Response quảng cáo")
public class AdvertisementResponse {
    @Schema(description = "ID quảng cáo")
    private String id;

    @Schema(description = "Tên quảng cáo")
    private String title;

    @Schema(description = "Nội dung quảng cáo")
    private String content;

    @Schema(description = "URL ảnh quảng cáo")
    private String imageUrl;

    @Schema(description = "Thời gian bắt đầu")
    private LocalDateTime startDate;

    @Schema(description = "Thời gian kết thúc")
    private LocalDateTime endDate;

    @Schema(description = "Trạng thái")
    private AdvertisementStatus status;

    @Schema(description = "ID đối tác")
    private String clientId;

    @Schema(description = "Người tạo")
    private String createdBy;

    @Schema(description = "Ngày tạo")
    private LocalDateTime createdDate;

    @Schema(description = "Người sửa")
    private String modifiedBy;

    @Schema(description = "Ngày sửa")
    private LocalDateTime modifiedDate;
}


