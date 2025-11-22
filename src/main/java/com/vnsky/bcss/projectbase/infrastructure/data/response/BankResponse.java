package com.vnsky.bcss.projectbase.infrastructure.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin ngân hàng từ VietQR API")
public class BankResponse {
    @Schema(description = "Mã ngân hàng")
    private String code;

    @Schema(description = "Tên ngân hàng")
    private String name;

    @Schema(description = "Tên viết tắt")
    private String shortName;

    @Schema(description = "Logo URL")
    private String logo;

    @Schema(description = "Tên tiếng Anh")
    private String nameEn;
}

