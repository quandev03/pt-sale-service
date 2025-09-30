package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AgentDebitDTO extends AbstractAuditDTO {
    @DbColumnMapper("ORG_ID")
    private String orgId;

    @DbColumnMapper("CLIENT_ID")
    private String clientId;

    @DbColumnMapper("AMOUNT")
    private Long amount;

    @DbColumnMapper("DEBT_LIMIT")
    private Long debtLimit;

    @DbColumnMapper("DEBT_LIMIT_MBF")
    private Long debtLimitMbf;

    @DbColumnMapper("PAYMENT_ID")
    private String paymentId;

    @DbColumnMapper("TYPE")
    private String type;
}
