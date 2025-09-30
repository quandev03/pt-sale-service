package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAvailablePackagesMbfResponse {
    
    private String code;
    private String message;
    private List<PackageInfo> packages;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PackageInfo {
        private String packageId;
        private String packageCode;
        private String packageName;
        private String description;
        private Long price;
        private Integer cycle;
        private String unit;
    }
}