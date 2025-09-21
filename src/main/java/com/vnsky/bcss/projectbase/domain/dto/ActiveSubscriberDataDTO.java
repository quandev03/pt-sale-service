package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.OcrStepResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActiveSubscriberDataDTO {
    @Schema(description = "Id của giao dịch")
    private String transactionId;

    @Schema(description = "Serial")
    private String serial;

    @Schema(description = "Số điện thoại")
    private Long isdn;

    @Schema(description = "Thông tin lấy được từ OCR")
    private OcrStepResponse.Infors ocrData;

    @Schema(description = "Link url của hộ chiếu")
    private String passportUrl;

    @Schema(description = "Link url của chân dung")
    private String portraitUrl;

    private ContractFileUrlDTO contractUrl;

    @Schema(description = "Bước thực hiện")
    private Integer stepActive;

    @Schema(description = "Thông tin đồng ý nghị định 13")
    private AgreeDegree13DTO agreeDegree13;

    private String idEkyc;
}
