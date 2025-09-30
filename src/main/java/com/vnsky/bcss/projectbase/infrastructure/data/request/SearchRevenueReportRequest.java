package com.vnsky.bcss.projectbase.infrastructure.data.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRevenueReportRequest {
    @Schema(description = "Từ khóa tìm kiếm theo mã đơn hàng", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String q;

    @Schema(description = "Tìm kiếm theo danh sách mã đối tác", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> orgCodes;

    @Schema(description = "Thời gian bắt đầu", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String startDate;

    @Schema(description = "Thời gian kết thúc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String endDate;

    @Schema(description = "Loại", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String type;
}
