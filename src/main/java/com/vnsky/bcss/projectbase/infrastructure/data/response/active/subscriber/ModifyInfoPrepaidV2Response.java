package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyInfoPrepaidV2Response {
    private String code;

    private String description;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Data{
        private String strSubId;

        private String strIsdn;
    }
}
