package com.vnsky.bcss.projectbase.infrastructure.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VietQRRequest {
    private String accountNo;
    private String accountName;
    private String acqId; // Bank code
    private BigDecimal amount;
    private String addInfo; // Content
    private String format; // text or qr
}

