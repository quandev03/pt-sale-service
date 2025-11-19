package com.vnsky.bcss.projectbase.domain.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PartnerPackageSubscriptionCreateCommand {
    String organizationUnitId;
    String packageProfileId;
    LocalDateTime startTime;
}

