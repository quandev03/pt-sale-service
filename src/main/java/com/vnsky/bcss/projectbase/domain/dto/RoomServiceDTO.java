package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomServiceDTO extends AbstractAuditDTO {
    private String id;
    private String orgUnitId;
    private RoomServiceType serviceType;
    private String serviceCode;
    private String serviceName;
    private BigDecimal price;
    private String clientId;
    private Integer status;
}

