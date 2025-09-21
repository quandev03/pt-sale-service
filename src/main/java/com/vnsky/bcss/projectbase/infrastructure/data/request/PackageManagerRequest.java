package com.vnsky.bcss.projectbase.infrastructure.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageManagerRequest {
    private String packageName;
    private String packageCode;
    private Long packagePrice;
    private String descriptionPackage;
}
