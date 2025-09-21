package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsimProcessingResult {
    private List<EsimBookingResult> successfulResults;
    private List<EsimErrorInfo> errorInfos;
} 