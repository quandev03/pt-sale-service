package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModifyInfoCorpResponse {
    private String code;

    private String description;

    private List<Data> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Data{
        private String strSubId;

        private String strIsdn;
    }
}
