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
    private Long isdn;

    @JsonProperty("CHANNEL_CODE")
    private String channelCode;

    @JsonProperty("ACCEPTED_VALUE")
    private String acceptedValue;

    @JsonProperty("ACCEPTED_DATETIME")
    private String acceptedDateTime;
}
