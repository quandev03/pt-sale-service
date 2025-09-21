package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterPackageMbfResponse extends BaseMbfResponse<RegisterPackageMbfResponse.PackageData> {

    @Data
    public static class PackageData {
        private String pckCode;
        private String pricePck;
    }
} 