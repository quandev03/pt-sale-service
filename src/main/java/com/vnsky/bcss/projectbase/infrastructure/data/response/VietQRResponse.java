package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VietQRResponse {
    private String code;
    private String desc;
    private VietQRData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VietQRData {
        private String qrCode;
        
        @JsonProperty("qrDataURL")
        private String qrDataURL;
    }
}

