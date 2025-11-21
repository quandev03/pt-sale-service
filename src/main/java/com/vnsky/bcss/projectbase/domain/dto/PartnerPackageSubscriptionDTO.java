package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerPackageSubscriptionDTO extends AbstractAuditDTO {
    private String id;
    private String organizationUnitId;
    private String packageProfileId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PartnerPackageSubscriptionStatus status;
}





