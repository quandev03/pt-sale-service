package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FaceCheckResponse {
    private String message;

    private String errCode;

    private String errorMessages;

    @JsonProperty("id_ekyc")
    private String idEkyc;

    private Data data;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Data{
        private Face face;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Face{
        private String confidence;
    }
}
