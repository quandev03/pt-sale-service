package com.vnsky.bcss.projectbase.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment.vnpay")
public class VNPayProperties {

    /**
     * Terminal code provided by VNPay (vnp_TmnCode).
     */
    private String tmnCode;

    /**
     * Secret key for checksum generation (vnp_HashSecret).
     */
    private String hashSecret;

    /**
     * VNPay payment URL (vnp_Url).
     */
    private String url;

    /**
     * Client return URL after payment (vnp_ReturnUrl).
     */
    private String returnUrl;

    /**
     * IPN (Instant Payment Notification) callback endpoint.
     */
    private String ipnUrl;

    /**
     * API version, default 2.1.0.
     */
    private String version;

    /**
     * Command, usually pay.
     */
    private String command;

    /**
     * Order type configured in VNPay dashboard.
     */
    private String orderType;

    /**
     * Currency code (e.g., VND).
     */
    private String currency;

    /**
     * Locale (e.g., vn).
     */
    private String locale;
}

