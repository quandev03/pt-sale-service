package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubmitDecree13Response {
    private String code;
    private String description;
    private List<Data> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Data{
        private String resultCode;

        @JsonProperty("sub_id")
        private String subId;

        @JsonProperty("mobitype")
        private String mobiType;

        @JsonProperty("rec_seq")
        private String recSeq;

        @JsonProperty("error_des")
        private String errorDes;
    }
}
