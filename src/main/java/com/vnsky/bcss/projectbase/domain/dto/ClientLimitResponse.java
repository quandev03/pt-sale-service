package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLimitResponse {
    
    @Schema(description = "Response code")
    private String code;
    
    @Schema(description = "Response message")
    private String message;
    
    @Schema(description = "Debit limit")
    private Long debitLimit;
    
    @Schema(description = "Debit limit MBF")
    private Long debitLimitMbf;
}
