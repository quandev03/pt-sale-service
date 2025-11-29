package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class DataOCRField {
    @JsonProperty("ocr_front")
    private DataOCRTwoSideField ocrFront;
    @JsonProperty("ocr_back")
    private DataOCRTwoSideField ocrBack;
}
