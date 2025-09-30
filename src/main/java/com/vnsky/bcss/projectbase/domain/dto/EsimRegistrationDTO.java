package com.vnsky.bcss.projectbase.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EsimRegistrationDTO extends AbstractAuditDTO {
    private String id;
    private String orderId;
    private LocalDateTime finishedDate;
    private Integer successedNumber;
    private Integer failedNumber;
    private Integer bookEsimStatus;
}
