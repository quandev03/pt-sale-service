package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmOTPMbfRequest {
    
    private String id;
    private String isdn;
    private String idEkyc;
    private String transactionId;
    private String otp;
    private Boolean getSuccess;
}