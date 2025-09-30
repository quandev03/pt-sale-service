package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckPackageEligibilityMbfRequest {
    
    private String isdn;
    private String packageId;
    private Boolean getSuccess;
}