package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vnsky.bcss.projectbase.shared.utils.CustomDateDeserializer;
import com.vnsky.bcss.projectbase.shared.utils.CustomDateSerialize;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PartnerPackageSubscriptionView extends CommonDTO {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("ORG_UNIT_ID")
    private String organizationUnitId;

    @DbColumnMapper("ORG_NAME")
    private String organizationUnitName;

    @DbColumnMapper("PACKAGE_PROFILE_ID")
    private String packageProfileId;

    @DbColumnMapper("PACKAGE_NAME")
    private String packageName;

    @DbColumnMapper("START_TIME")
    @JsonSerialize(using = CustomDateSerialize.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime startTime;

    @DbColumnMapper("END_TIME")
    @JsonSerialize(using = CustomDateSerialize.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime endTime;

    @DbColumnMapper("STATUS")
    private String status;
}





