package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartnerPackageSubscriptionPaymentReturnResult {
    String subscriptionId;
    String txnRef;
    String responseCode;
    String message;
    PartnerPackageSubscriptionPaymentStatus status;
}

