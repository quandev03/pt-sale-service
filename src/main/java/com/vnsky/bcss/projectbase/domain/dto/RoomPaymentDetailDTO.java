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
public class RoomPaymentDetailDTO extends AbstractAuditDTO {
    private String id;
    private String roomPaymentId;
    private String roomServiceId;
    private RoomServiceType serviceType;
    private String serviceName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}

