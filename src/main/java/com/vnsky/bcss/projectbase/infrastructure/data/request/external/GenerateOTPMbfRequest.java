package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateOTPMbfRequest {
    
    private String isdn;
    private String otpType;
    private String transactionId;
    private String idEkyc;
    private Boolean getSuccess;
}