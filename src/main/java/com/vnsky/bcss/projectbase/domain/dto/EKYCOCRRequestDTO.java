package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EKYCOCRRequestDTO {

    @JsonProperty("card_type")
    private int cardType;

    @JsonProperty("card_front")
    private String cardFront;

    @JsonProperty("card_back")
    private String cardBack;
}
