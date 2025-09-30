package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterPackageMbfRequest {
    private String strIsdn;
    private String strShopCode;
    private String strEmployee;
    private List<String> arrRegPck;
    private List<String> arrDelPck;
}
