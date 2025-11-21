package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VNPayIpnResponse {
    @JsonProperty("RspCode")
    String rspCode;
    @JsonProperty("Message")
    String message;
}

