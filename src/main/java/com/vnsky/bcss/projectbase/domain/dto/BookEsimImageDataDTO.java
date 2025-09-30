package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookEsimImageDataDTO {
    
    @Schema(description = "Transaction ID for tracking")
    private String transactionId;
    
    @Schema(description = "ISDN number")
    private Long isdn;
    
    @Schema(description = "URL for CCCD front image (type = 0)")
    private String cccdFrontUrl;
    
    @Schema(description = "URL for CCCD back image (type = 0)")
    private String cccdBackUrl;
    
    @Schema(description = "URL for portrait image (type = 1)")
    private String portraitUrl;
    
    @Schema(description = "URL for contract image (type = 2)")
    private String contractUrl;
    
    @Schema(description = "Agree decree 13 information")
    private AgreeDecree13DTO agreeDegree13;
    
    @Schema(description = "Contract code (ULID)")
    private String contractCode;
}
