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
public class SubmitDecree13Request {
    private String isdn;

    private String subId;

    private String contractId;

    private String tc1;

    private String tc2;

    private String tc3;

    private String tc4;

    private String tc5;

    private String noteDesc;
}
