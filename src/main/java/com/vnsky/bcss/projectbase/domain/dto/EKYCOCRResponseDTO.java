package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EKYCOCRResponseDTO {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errCode")
    private String errCode;

    @JsonProperty("data_ocr")
    private DataOCRField dataOcr;

    @JsonProperty("id_ekyc")
    private String idEkyc;
}
