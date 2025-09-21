package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimMbfRequest {

    @JsonProperty("profile_type")
    private String profileType;

    @JsonProperty("getSuccess")
    private Boolean getSuccess;
} 