package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateOTPMbfResponse {
    
    private String code;
    private String message;
    private String id;
    private String isdn;
    private String idEkyc;
    private String transactionId;
}