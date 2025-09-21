package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OcrAndFaceCheckResponse {
    @Schema(description = "Trạng thái thực hiện (1 là thành công, 2 là thất bại)", example = "0")
    private Integer status;

    @Schema(description = "Thất bại tại bước nào, (1 là OCR, 2 là Face Check)")
    private Integer failedStep;

    @Schema(description = "Id của giao dịch, các api sau sẽ cần truyền cái này")
    private String transactionId;

    @JsonIgnore
    private String idEkyc;

    private String message;

    private String serial;

    private OcrStepResponse.Infors ocrData;
}
