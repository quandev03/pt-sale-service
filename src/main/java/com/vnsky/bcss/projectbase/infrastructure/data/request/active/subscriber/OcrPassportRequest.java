package com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OcrPassportRequest {
    @JsonProperty("card_type")
    private Integer cardType;

    @JsonProperty("card_front")
    private String cardFront;
}
