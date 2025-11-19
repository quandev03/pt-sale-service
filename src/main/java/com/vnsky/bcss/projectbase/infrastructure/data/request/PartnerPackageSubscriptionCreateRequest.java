package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PartnerPackageSubscriptionCreateRequest {

    @NotBlank
    private String organizationUnitId;

    @NotBlank
    private String packageProfileId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
}

