package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import java.util.Arrays;

public enum PartnerPackageSubscriptionStatus {
    PENDING_PAYMENT,
    ACTIVE,
    INACTIVE,
    EXPIRED,
    FAILED;

    public static PartnerPackageSubscriptionStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown subscription status: " + value));
    }
}


