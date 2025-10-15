package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvincesResponse {
    private String requestId;
    private List<ProvinceDto> provinces;
}
