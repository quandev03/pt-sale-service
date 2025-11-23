package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
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
public class AdvertisementDTO extends AbstractAuditDTO {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AdvertisementStatus status;
    private String clientId;
}

