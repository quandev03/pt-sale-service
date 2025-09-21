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
public class FaceCheckRequest {
    @JsonProperty("id_ekyc")
    private String idEkyc;

    @JsonProperty("anh_thang")
    private String anhThang;
}
