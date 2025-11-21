package com.vnsky.bcss.projectbase.domain.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PartnerPackageSubscriptionPaymentCommand {
    String organizationUnitId;
    String packageProfileId;
    LocalDateTime startTime;
    String clientIp;
    String returnUrl;
}

