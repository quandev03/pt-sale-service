package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommuneDto {
    private String code;
    private String name;
    private String englishName;
    private String administrativeLevel;
    private String provinceCode;
    private String provinceName;
    private String decree;
}
