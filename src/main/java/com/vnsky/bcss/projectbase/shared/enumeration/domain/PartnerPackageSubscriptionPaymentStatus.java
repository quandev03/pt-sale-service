package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import java.util.Arrays;

public enum PartnerPackageSubscriptionPaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELED;

    public static PartnerPackageSubscriptionPaymentStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown payment status: " + value));
    }
}

