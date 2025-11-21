package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vnsky.bcss.projectbase.shared.utils.IsoLocalDateTimeDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PartnerPackageSubscriptionPaymentRequest {

    @NotBlank
    private String organizationUnitId;

    @NotBlank
    private String packageProfileId;

    @JsonDeserialize(using = IsoLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    private String clientIp;

    private String returnUrl;
}


