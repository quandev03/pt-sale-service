package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsimErrorInfo {
    private String isdn;
    private int errorStatus;
    private String errorMessage;
    private EsimRegistrationLineDTO registrationLine;
} 