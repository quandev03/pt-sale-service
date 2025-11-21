package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartnerPackageSubscriptionPaymentResponse {
    String subscriptionId;
    String paymentId;
    String txnRef;
    Long amount;
    String orderInfo;
    String paymentUrl;
    PartnerPackageSubscriptionPaymentStatus status;
}

