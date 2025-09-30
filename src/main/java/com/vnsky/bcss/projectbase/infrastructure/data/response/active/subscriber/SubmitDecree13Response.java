package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubmitDecree13Response {
    private String code;
    private String description;
    private List<Data> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    @Builder
    public static class Data{
        private String result;

        private String error;
    }
}
