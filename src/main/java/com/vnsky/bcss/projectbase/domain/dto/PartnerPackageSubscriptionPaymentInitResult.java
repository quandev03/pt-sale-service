package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartnerPackageSubscriptionPaymentInitResult {
    String subscriptionId;
    String paymentId;
    String txnRef;
    Long amount;
    String orderInfo;
    String paymentUrl;
    PartnerPackageSubscriptionPaymentStatus status;
}

