package com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseIntegrationRequest {

    @NotNull(message = "cmd không được để trống")
    @Schema(description = "Command", example = "MBF")
    private String cmd;

    @Schema(description = "Loại api trong từng cmd nếu có", example = "INFO")
    private String type;

    // mở rộng thêm cho các tích hợp khác
    @Schema(description = "mở rộng thêm để tích hợp với nhiều api khác động hơn ")
    private Object extraInfo;

    @Schema(description = "Dữ liệu chính truyền vào")
    private Object data;
}
