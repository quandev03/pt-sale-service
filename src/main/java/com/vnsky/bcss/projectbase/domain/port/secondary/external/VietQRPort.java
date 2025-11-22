package com.vnsky.bcss.projectbase.domain.port.secondary.external;

import com.vnsky.bcss.projectbase.infrastructure.data.response.VietQRResponse;

import java.math.BigDecimal;

public interface VietQRPort {
    VietQRResponse generateQRCode(String accountNo, String accountName, BigDecimal amount, String content, String bankCode);
}

