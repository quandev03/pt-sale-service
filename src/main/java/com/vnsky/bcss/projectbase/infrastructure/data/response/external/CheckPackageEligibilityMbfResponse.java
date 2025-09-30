package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckPackageEligibilityMbfResponse {
    
    private String code;
    private String message;
    private Boolean eligible;
    private String isdn;
    private String packageId;
}